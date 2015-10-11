# SimpleDB Crud

User interface for working with SimpleDB. The missing console plug-in in the AWS tool set.

![Alt UI Preview](http://therealvan.com/image/projects/simpledb_crud.png)


## Prerequisite
* AWS account set up and activated.
* Java JRE 1.8+
* If building your own instance:
    * Maven (3.3+)
    * JDK 1.8+

## Setup

The Webapp uses the [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/), so the normal set-up for the
AWS credentials will work. For example:

### Windows
Create a credentials file as: `%USERPROFILE%\.aws\credentials`

    [default]
    aws_access_key_id = xxxxxxxxxx
    aws_secret_access_key = yyyyyyyyyyyy


### Unix
Create the credentials file as: `~/.aws/credentials` in the home directory of the user running the Web app.

### AWS EC2
Create a valid [Instance Profile](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_use_switch-role-ec2_instance-profiles.html) for your EC2 instances.

## Building & Running

### Spring Boot
To build the project, simple clone the product onto your local drive. Then run the Maven target:

    git clone 
    cd simpledb-crud
    mvn spring-boot:run
    
Access the Webapp via: [http://localhost:8080/home/index](http://localhost:8080/home/index)

### Runnable Jar
To build the project as a runnable jar, simple clone the product onto your local drive. Then run the Maven targets:

    git clone 
    cd simpledb-crud
    mvn install package spring-boot:repackage
    java -jar target/simpledb-crud-0.0.1-SNAPSHOT.jar
    
Access the Webapp via: [http://localhost:8080/home/index](http://localhost:8080/home/index)

### Alternate port
Yes yes yes. Port 8080 is so popular that&mdash;chances are you already have something using 8080. Since the tool
is based on Spring Boot, just add this flag to set the port:

    -Dserver.port=8090
    
    