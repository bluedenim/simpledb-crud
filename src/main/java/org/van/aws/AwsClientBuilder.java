package org.van.aws;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;

/**
 * Builder to build various AWS clients.  In addition to using the default
 * credential providers that the AWS JDK supports, this builder also allows
 * passing in the credentials for applications that prefer to do so.
 * 
 * @author vly
 *
 */
public class AwsClientBuilder {
	private static final String NONE = null;
	
	private final String accessKey;
	private final String secretKey;
	private final Region region;

    private static final List<Region> REGIONS = new LinkedList<>();

    static {
        REGIONS.addAll(RegionUtils.getRegions());
    }
	
	/**
	 * Instantiate a builder with the credentials and region specified.
	 * 
	 * @param region the region in which to create clients for
	 * @param accessKey the access key to use for credentials
	 * @param secretKey the secret key to use for credentials
	 */
	public AwsClientBuilder(Region region, String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;
	}

    /**
     * Returns all the regions that we know about
     *
     * @return regions that we know about
     */
    public static List<Region> getRegions() {
        return Collections.unmodifiableList(REGIONS);
    }

	/**
	 * Instantiate a builder using the default AWS credential sources
	 * 
	 * @param region the region in which to create clients for
	 */
	public AwsClientBuilder(Region region) {
		this(region, NONE, NONE);
	}
	
	/**
	 * Build a service client class using the parameters set up for the builder
	 * 
	 * @param clientClass the class of the service client to build
	 * 
	 * @return an instance of the service client 
	 */
	public <T extends AmazonWebServiceClient> T build(Class<T> clientClass) {
		return build(clientClass, false);
	}
	
	/**
	 * Build a service client using the parameters set up for the builder, with
	 * an option to skip setting the region of the client instance.  This may
	 * be useful for some clients such as the AmazonS3Client when accessing
	 * buckets in the "standard" region.
	 * 
	 * @param clientClass the client class
	 * @param skipRegionInit if true, do not initialize the client with the
	 *     region from the builder
	 *     
	 * @return an instance of the service client
	 */
	public <T extends AmazonWebServiceClient> T build(Class<T> clientClass, 
		boolean skipRegionInit) {
		T client = null;
		try {
            if (skipRegionInit) {
                Constructor<T> ctor = clientClass.getConstructor(AWSCredentialsProvider.class);
                client = ctor.newInstance(getCredentialsProvider());
            } else {
                client = region.createClient(clientClass, null, null);
            }
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot build client", e);
		}
		return client;
	}
	
	AWSCredentialsProvider getCredentialsProvider() {
		AWSCredentialsProvider credProvider = null;
		if ((NONE != accessKey) && (NONE != secretKey)) {
			credProvider = new AWSCredentialsProviderChain(
				// If the user took the pains to construct us with the access
				// credentials, give them priority over the defaults from the
				// the more general environment
				new AWSCredentialsProvider() {
					public AWSCredentials getCredentials() {
						return new BasicAWSCredentials(accessKey, secretKey);
					}

					public void refresh() {
					}
				},
				new DefaultAWSCredentialsProviderChain()					
			);
		} else {
			credProvider = new DefaultAWSCredentialsProviderChain();
		}
		return credProvider;
	}
}
