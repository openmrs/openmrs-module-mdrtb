#!/bin/bash 

# Bash script to convert OpenMRS module to a Maven multi-module project
# Author: Matthew Blanchette

########## CONFIG  ##########

SH_OPENMRS_VERSION=1.8.2

SH_MOD_ID=`grep -m 1 "<id>" metadata/config.xml | sed 's/^[\t ]*<.*>\([^<]*\)<.*>[\t ]*/\1/' | tr -d '\r\n' `
echo "id: $SH_MOD_ID"

SH_MOD_NAME=`grep -m 1 "<name>" metadata/config.xml | sed 's/^[\t ]*<.*>\([^<]*\)<.*>[\t ]*/\1/' | tr -d '\r\n' `
echo "name: $SH_MOD_NAME"

SH_VERSION_SUFFIX=-SNAPSHOT

SH_MOD_VERSION=`grep -m 1 "<version>" metadata/config.xml | sed 's/^[\t ]*<.*>\([^<]*\)<.*>[\t ]*/\1/' | tr -d '\r\n' `
SH_MOD_VERSION="$SH_MOD_VERSION$SH_VERSION_SUFFIX"
echo "version: $SH_MOD_VERSION"

SH_LIB=`find lib -not \( -name .svn -prune \) -type f -name "*.jar"`
echo "lib to add as dependencies: $SH_LIB"

SH_SRC_RES=`find src -not \( -name .svn -prune \) -type f -not \( -name "*.java" \) | wc -l`
echo "Number of src resources: $SH_SRC_RES"

SH_TEST_RES=`find test -not \( -name .svn -prune \) -type f -not \( -name "*.java" \) | wc -l`
echo "Number of test resources: $SH_TEST_RES"

SH_WEB_SRC_RES=`find web/src -not \( -name .svn -prune \) -type f -not \( -name "*.java" \) | wc -l`
echo "Number of web src resources: $SH_WEB_SRC_RES"

########## OMOD MODULE ##########

echo "Creating omod project directory structure"
svn mkdir omod/src/main --parents
svn mkdir omod/src/test

echo "Moving metadata to omod resources"
svn move metadata omod/src/main/resources

echo "Moving web resources to omod webapp"
svn move web/module omod/src/main/webapp

if [[ "$SH_WEB_SRC_RES" != "0" ]]; then
  echo "Moving web resources to omod resources"
  for f in `find web/src -not \( -name .svn -prune \) -maxdepth 0`; do
    SH_F=`echo $f | sed 's/web\/src//'`
    svn copy $f omod/src/main/resources$SH_F
  done

  echo "Removing java from omod main resources"
  for f in `find omod/src/main/resources -not \( -name .svn -prune \) -type f -name "*.java"`; do
    svn delete $f
  done
fi

echo "Moving web java src to omod java"
svn move web/src omod/src/main/java

if [[ "$SH_WEB_SRC_RES" != "0" ]]; then
  echo "Removing resources from omod main java"
  for f in `find omod/src/main/java -not \( -name .svn -prune \) -type f -not \( -name "*.java" \)`; do
    svn delete $f
  done
fi


echo "Moving all test java and resources to omod"

if [[ "$SH_TEST_RES" != "0" ]]; then 
  echo "Moving test resources to omod test resources"
  svn copy test omod/src/test/resources
  
  echo "Removing java from omod test resources"
  for f in `find omod/src/test/resources -not \( -name .svn -prune \) -type f -name "*.java"`; do
    svn delete $f
  done
fi

svn move test omod/src/test/java

if [[ "$SH_TEST_RES" != "0" ]]; then 
  echo "Removing resources from omod test java"
  for f in `find omod/src/test/java -not \( -name .svn -prune \) -type f -not \( -name "*.java" \)`; do
    svn delete $f
  done
fi


echo "Modifying config.xml in omod resources to use properties"

echo "Replacing id, name, version, and package with properties"
sed -i '0,/<id>[^<]*<\/id>/s//<id>@MODULE_ID@<\/id>/' omod/src/main/resources/config.xml
sed -i '0,/<name>[^<]*<\/name>/s//<name>@MODULE_NAME@<\/name>/' omod/src/main/resources/config.xml
sed -i '0,/<version>[^<]*<\/version>/s//<version>@MODULE_VERSION@<\/version>/' omod/src/main/resources/config.xml
sed -i '0,/<package>[^<]*<\/package>/s//<package>@MODULE_PACKAGE@<\/package>/' omod/src/main/resources/config.xml

echo "Replacing hibernate mappings with omodHbmConfig property"
# Replace first occurance of hbm.xml with property, Remove other occurances
sed -i -e '0,/\([\t ]*\).*\.hbm\.xml/s//\1\${omodHbmConfig}/1;/.*\.hbm\.xml/d' omod/src/main/resources/config.xml


echo "Adding hibernate and spring resources for context-sensitive tests"
# Only create and add test resources dir if not already
if [ ! -d "omod/src/test/resources" ]; then
  svn mkdir omod/src/test/resources
fi

# Only create and add hibernate/spring test resources if not already exist

if [ ! -f "omod/src/test/resources/TestingApplicationContext.xml" -a ! -f "omod/src/test/resources/test-hibernate.cfg.xml" ]; then
  
  echo "Writing out and adding hibernate test config"
  
  echo '<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
	"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
	"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">

<hibernate-configuration>
	<session-factory>
		${hbmConfig}
	</session-factory>
</hibernate-configuration>' > omod/src/test/resources/test-hibernate.cfg.xml
  
  svn add omod/src/test/resources/test-hibernate.cfg.xml
  
  echo "Writing out and adding spring test context"
  
  echo '<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:util="http://www.springframework.org/schema/util"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
		http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
		http://www.springframework.org/schema/context
		http://www.springframework.org/schema/context/spring-context-3.0.xsd
		http://www.springframework.org/schema/util
		http://www.springframework.org/schema/util/spring-util-3.0.xsd">
	
	<!-- 
	From applicationContext-service.xml in openmrs-api
	Needed to override and add hibernate mappings to the classpath since omod is not packaged yet 
	-->
	<bean id="sessionFactory" class="org.openmrs.api.db.hibernate.HibernateSessionFactoryBean">
		<property name="configLocations">
			<list>
				<value>classpath:hibernate.cfg.xml</value>
				<value>classpath:test-hibernate.cfg.xml</value>
			</list>
		</property>
		<property name="mappingJarLocations">
			<ref bean="mappingJarResources" />
		</property>
		<!--  default properties must be set in the hibernate.default.properties -->
	</bean>
	
</beans>' > omod/src/test/resources/TestingApplicationContext.xml
  
  svn add omod/src/test/resources/TestingApplicationContext.xml
  
fi

########## API MODULE ##########

echo "Creating api project directory structure"
svn mkdir api/src/main --parents
svn mkdir api/src/test

if [[ "$SH_SRC_RES" != "0" ]]; then
  echo "Moving src resources to api main resources"
  svn copy src api/src/main/resources
  
  echo "Removing java from api main resources"
  for f in `find api/src/main/resources -not \( -name .svn -prune \) -type f -name "*.java"`; do
    svn delete $f
  done
fi

echo "Moving src and test to api java"
svn move src api/src/main/java

if [[ "$SH_SRC_RES" != "0" ]]; then
  echo "Removing resources from api main java"
  for f in `find api/src/main/java -not \( -name .svn -prune \) -type f -not \( -name "*.java" \)`; do
    svn delete $f
  done
fi

########## OMOD POM ##########

echo "Writing out and adding omod pom"

OMOD_POM=$(cat << EOF
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.openmrs.module</groupId>
		<artifactId>${SH_MOD_ID}-parent</artifactId>
		<version>${SH_MOD_VERSION}</version>
	</parent>
	<groupId>org.openmrs.module</groupId>
	<artifactId>${SH_MOD_ID}</artifactId>
	<packaging>omod</packaging>
	<name>${SH_MOD_NAME} OMOD</name>
	<description>OpenMRS module project for ${SH_MOD_NAME}</description>

	<dependencies>
		<dependency>
			<groupId>org.openmrs.module</groupId>
			<artifactId>${SH_MOD_ID}-api</artifactId>
			<version>\${project.parent.version}</version>
		</dependency>
		
		<dependency>
			<groupId>org.openmrs.api</groupId>
			<artifactId>openmrs-api</artifactId>
			<type>jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.api</groupId>
			<artifactId>openmrs-api</artifactId>
			<type>test-jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.web</groupId>
			<artifactId>openmrs-web</artifactId>
			<type>jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.web</groupId>
			<artifactId>openmrs-web</artifactId>
			<type>test-jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.test</groupId>
			<artifactId>openmrs-test</artifactId>
			<type>pom</type>
		</dependency>
	</dependencies>

	<build>
		<resources>
			<resource>
				<directory>src/main/resources</directory>
				<filtering>true</filtering>
			</resource>
			<resource>
				<directory>src/main/webapp</directory>
				<filtering>true</filtering>
				<excludes>
					<exclude>resources</exclude>
				</excludes>
				<targetPath>web/module</targetPath>
			</resource>
			<resource>
				<directory>src/main/webapp</directory>
				<filtering>false</filtering>
				<includes>
					<include>resources</include>
				</includes>
				<targetPath>web/module</targetPath>
			</resource>
		</resources>
		<testResources>
			<testResource>
				<directory>src/test/resources</directory>
				<filtering>true</filtering>
				<includes>
					<include>**/*.xml</include>
					<include>**/*.txt</include>
				</includes>
			</testResource>
			<testResource>
				<directory>src/test/resources</directory>
				<filtering>false</filtering>
				<excludes>
					<exclude>**/*.xml</exclude>
					<exclude>**/*.txt</exclude>
				</excludes>
			</testResource>
		</testResources>
		<pluginManagement>
			<plugins>
				<plugin>
					<artifactId>maven-resources-plugin</artifactId>
					<configuration>
						<includeEmptyDirs>true</includeEmptyDirs>
					</configuration>
				</plugin>
			</plugins>
		</pluginManagement>
		<plugins>
			<plugin>
				<groupId>org.openmrs.maven.plugins</groupId>
				<artifactId>maven-openmrs-plugin</artifactId>
				<extensions>true</extensions>
				<configuration>
					<verifyOmod>false</verifyOmod>
				</configuration>
			</plugin>
		</plugins>
	</build>

	<properties>
		<MODULE_ID>\${project.artifactId}</MODULE_ID>
		<MODULE_NAME>\${project.parent.name}</MODULE_NAME>
		<MODULE_VERSION>\${project.parent.version}</MODULE_VERSION>
		<MODULE_PACKAGE>\${project.parent.groupId}.\${project.artifactId}</MODULE_PACKAGE>
	</properties>

</project>
EOF
)
echo "$OMOD_POM" > omod/pom.xml

svn add omod/pom.xml

########## API POM ##########

echo "Writing out and adding api pom"

API_POM=$(cat << EOF
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.openmrs.module</groupId>
		<artifactId>${SH_MOD_ID}-parent</artifactId>
		<version>${SH_MOD_VERSION}</version>
	</parent>
	<groupId>org.openmrs.module</groupId>
	<artifactId>${SH_MOD_ID}-api</artifactId>
	<packaging>jar</packaging>
	<name>${SH_MOD_NAME} API</name>
	<description>API project for ${SH_MOD_NAME}</description>

	<dependencies>
		<!-- 
		Add other dependencies from lib:
		<dependency>
			<groupId>org.other.library</groupId>
			<artifactId>library-name</artifactId>
		</dependency>
		-->
		
		<dependency>
			<groupId>org.openmrs.api</groupId>
			<artifactId>openmrs-api</artifactId>
			<type>jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.api</groupId>
			<artifactId>openmrs-api</artifactId>
			<type>test-jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.web</groupId>
			<artifactId>openmrs-web</artifactId>
			<type>jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.web</groupId>
			<artifactId>openmrs-web</artifactId>
			<type>test-jar</type>
		</dependency>
		<dependency>
			<groupId>org.openmrs.test</groupId>
			<artifactId>openmrs-test</artifactId>
			<type>pom</type>
		</dependency>
	</dependencies>

</project>
EOF
)
echo "$API_POM" > api/pom.xml

svn add api/pom.xml

########## PARENT POM ##########

echo "Writing out and adding parent pom"

POM=$(cat << EOF
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>org.openmrs.module</groupId>
	<artifactId>${SH_MOD_ID}-parent</artifactId>
	<version>${SH_MOD_VERSION}</version>
	<packaging>pom</packaging>
	<name>${SH_MOD_NAME}</name>
	<description>Parent project for ${SH_MOD_NAME}</description>

	<url>http://openmrs.org</url>

	<scm>
		<connection>scm:svn:http://svn.openmrs.org/openmrs-modules/${SH_MOD_ID}/trunk/</connection>
		<developerConnection>scm:svn:http://svn.openmrs.org/openmrs-modules/${SH_MOD_ID}/trunk/</developerConnection>
		<url>http://svn.openmrs.org/openmrs-modules/${SH_MOD_ID}/trunk/</url>
	</scm>

	<modules>
		<module>api</module>
		<module>omod</module>
	</modules>

	<dependencyManagement>
		<dependencies>
			<!--
			Libraries from lib to add as dependencies with compile or runtime scope:
			${SH_LIB}
				
			Find matching dependencies in maven central repository.
			<dependency>
				<groupId>org.other.library</groupId>
				<artifactId>library-name</artifactId>
				<version>library.version</version>
				<scope>compile</scope>
			</dependency>
			-->
			
			<dependency>
				<groupId>org.openmrs.api</groupId>
				<artifactId>openmrs-api</artifactId>
				<version>\${openMRSVersion}</version>
				<type>jar</type>
				<scope>provided</scope>
			</dependency>
			<dependency>
				<groupId>org.openmrs.api</groupId>
				<artifactId>openmrs-api</artifactId>
				<version>\${openMRSVersion}</version>
				<type>test-jar</type>
				<scope>test</scope>
			</dependency>
			<dependency>
				<groupId>org.openmrs.web</groupId>
				<artifactId>openmrs-web</artifactId>
				<version>\${openMRSVersion}</version>
				<type>jar</type>
				<scope>provided</scope>
			</dependency>
			<dependency>
				<groupId>org.openmrs.web</groupId>
				<artifactId>openmrs-web</artifactId>
				<version>\${openMRSVersion}</version>
				<type>test-jar</type>
				<scope>test</scope>
			</dependency>
			<dependency>
				<groupId>org.openmrs.test</groupId>
				<artifactId>openmrs-test</artifactId>
				<version>\${openMRSVersion}</version>
				<type>pom</type>
				<scope>test</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<properties>
		<openMRSVersion>${SH_OPENMRS_VERSION}</openMRSVersion>
	</properties>

	<build>
		<pluginManagement>
			<plugins>
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-compiler-plugin</artifactId>
					<configuration>
						<target>1.5</target>
						<source>1.5</source>
					</configuration>
				</plugin>
				<plugin>
					<groupId>org.openmrs.maven.plugins</groupId>
					<artifactId>maven-openmrs-plugin</artifactId>
					<version>1.0.1</version>
				</plugin>
			</plugins>
		</pluginManagement>
	</build>

	<repositories>
		<repository>
			<id>openmrs-repo</id>
			<name>OpenMRS Nexus Repository</name>
			<url>http://mavenrepo.openmrs.org/nexus/content/repositories/public</url>
		</repository>
	</repositories>

	<pluginRepositories>
		<pluginRepository>
			<id>openmrs-repo</id>
			<name>OpenMRS Nexus Repository</name>
			<url>http://mavenrepo.openmrs.org/nexus/content/repositories/public</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</pluginRepository>
	</pluginRepositories>

</project>
EOF
)
echo "$POM" > pom.xml

svn add pom.xml

########## CLEANUP ##########

echo "Removing original directories and files"

# No need to remove metdata, src, or test
# These should be moved already

# Removing now empty web directory
svn delete web

# Not removing Eclipse settings
#svn delete .settings

svn delete .classpath
svn delete .project

svn delete lib-common

# Not removing libraries unless empty
# These need to be added as dependencies
if [[ "$SH_LIB" == "" ]]; then
  svn delete lib
fi

# Removing build.xml
svn delete build.xml

########## SVN PROPS ##########

echo "Updating svn ignore properties in api and omod"

echo "Ignoring Maven target in omod"
svn propset svn:ignore "target
.settings
.classpath
.project
" omod

echo "Ignoring Maven target in api"
svn propset svn:ignore "target
.settings
.classpath
.project
" api

echo "Ignoring Maven target in parent dir"
# Overrides existing ignore properties
svn propset svn:ignore "build
dist
target
.settings
.classpath
.project
" .

########## EOF ##########
