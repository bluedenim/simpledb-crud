/**
 * 
 */
package org.van.simpledbui.services;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.van.aws.AwsClientBuilder;
import org.van.simpledbui.controllers.SimDb;

import javax.annotation.PostConstruct;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * @author vly
 *
 */
@Service
public class SimpleDbService {

    public static class PaginatedResult<T> {
        T result;
        String nextPageToken;

        PaginatedResult(T result, String nextPageToken) {
            this.result = result;
            this.nextPageToken = nextPageToken;
        }

        public T getResult() {
            return result;
        }

        public String getNextPageToken() {
            return nextPageToken;
        }
    }

    Region region;

    @PostConstruct
    public void init() {
        region = Region.getRegion(Regions.US_WEST_2);
    }

    public Region findRegion(String regionName) {
        Region region = null;
        if (null != regionName) {
            region = RegionUtils.getRegion(regionName);
        }
        return region;
    }

    public PaginatedResult<List<String>> listDomains(Region region) {
        AmazonSimpleDBClient client = createClient(region);
        ListDomainsResult result = client.listDomains();
        return new PaginatedResult<>(result.getDomainNames(), result.getNextToken());
    }

    public void createDomain(Region region, String name) {
        AmazonSimpleDBClient client = createClient(region);
        CreateDomainRequest request = new CreateDomainRequest(name);
        client.createDomain(request);
    }

    public void deleteDomain(Region region, String name) {
        AmazonSimpleDBClient client = createClient(region);
        DeleteDomainRequest request = new DeleteDomainRequest(name);
        client.deleteDomain(request);
    }

    public PaginatedResult<SelectResult> select(Region region, String domain, String query) {
        AmazonSimpleDBClient client = createClient(region);
        SelectRequest request =new SelectRequest()
            .withSelectExpression(query)
            .withConsistentRead(true);
        SelectResult result = client.select(request);
        return new PaginatedResult<>(result, result.getNextToken());
    }

    public void updateAttribute(Region region, String domain,
        String itemName, String attributeName,
        String attributeValue) {
        AmazonSimpleDBClient client = createClient(region);
        PutAttributesRequest request = new PutAttributesRequest(domain, itemName,
            composeReplaceableAttribs(attributeName, attributeValue));
        client.putAttributes(request);
    }

    public void addItem(Region region, String domain, SimDb.ItemData itemData) {
        if (StringUtils.isEmpty(itemData.itemName)) {
            throw new IllegalArgumentException("Item Name required");
        }
        AmazonSimpleDBClient client = createClient(region);
        PutAttributesRequest request = new PutAttributesRequest(domain, itemData.itemName,
            composeReplaceableAttribs(itemData.attributes));
        client.putAttributes(request);
    }

    public void deleteItem(Region region, String domain, String itemName) {
        AmazonSimpleDBClient client = createClient(region);
        DeleteAttributesRequest request = new DeleteAttributesRequest()
                .withDomainName(domain)
                .withItemName(itemName)
                ;
        client.deleteAttributes(request);
    }

    public void deleteAttribute(Region region, String domain, String itemName, String attribute) {
        AmazonSimpleDBClient client = createClient(region);
        DeleteAttributesRequest request = new DeleteAttributesRequest()
                .withDomainName(domain)
                .withItemName(itemName)
                .withAttributes(new Attribute().withName(attribute))
                ;
        client.deleteAttributes(request);
    }



    List<ReplaceableAttribute> composeReplaceableAttribs(String attributeName, String attributeValue) {
        List<ReplaceableAttribute> attribs = new LinkedList<>();
        attribs.add(new ReplaceableAttribute(attributeName, attributeValue, true));
        return attribs;
    }

    List<ReplaceableAttribute> composeReplaceableAttribs(SimDb.ItemAttribute attributes[]) {
        return asList(attributes).stream()
            .filter(itemAttrib -> !StringUtils.isEmpty(itemAttrib.name))
            .map(itemAttrib -> new ReplaceableAttribute(itemAttrib.name, itemAttrib.value, true))
            .collect(toList());
    }


    AmazonSimpleDBClient createClient(Region region) {
        if (null == region) {
            region = this.region;
        }
        AwsClientBuilder builder = new AwsClientBuilder(region);
        AmazonSimpleDBClient client = builder.build(AmazonSimpleDBClient.class);
        client.setRegion(region);
        return client;
    }


}
