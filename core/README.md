ner-base
========

This module uses Apache OpenNLP programatically to perform Named Entity
Recognition. 

+ Dutch and Spanish models trained using CoNLL 2002 dataset (75.92 F1 and 79.92 F1 respectively).
+ English and German models have been trained using CoNLL 2003 dataset (84.80 F1 and 61.48 F1 respectively).
+ Italian model trained using Evalita 2009 dataset (72.88 F1).
+ French model trained with ESTER corpus (80.59 F1). 

Contents
========

The contents of the module are the following:

- core: directory containing the NERC module

    + formatter.xml           Apache OpenNLP code formatter for Eclipse SDK
    + pom.xml                 maven pom file which deals with everything related to compilation and execution of the module
    + src/                    java source code of the module
    + Furthermore, the installation process, as described in the README.md, will generate another directory:
    target/                 it contains binary executable and other directories



INSTALLING Version 1.0 of ehu-nerc module
=========================================

Installing the ehu-nerc module requires the following steps:

If you already have installed in your machine JDK7 and MAVEN 3, please go to step 3
directly. Otherwise, follow these steps:

1. Install JDK 1.7
-------------------

If you do not install JDK 1.7 in a default location, you will probably need to configure the PATH in .bashrc or .bash_profile:

````shell
export JAVA_HOME=/yourpath/local/java7
export PATH=${JAVA_HOME}/bin:${PATH}
````

If you use tcsh you will need to specify it in your .login as follows:

````shell
setenv JAVA_HOME /usr/java/java17
setenv PATH ${JAVA_HOME}/bin:${PATH}
````

If you re-login into your shell and run the command

````shell
java -version
````

You should now see that your jdk is 1.7

2. Install MAVEN 3
------------------

Download MAVEN 3 from

````shell
wget http://ftp.udc.es/apache/maven/maven-3/3.0.4/binaries/apache-maven-3.0.4-bin.tar.gz
````

Now you need to configure the PATH. For Bash Shell:

````shell
export MAVEN_HOME=/home/ragerri/local/apache-maven-3.0.4
export PATH=${MAVEN_HOME}/bin:${PATH}
````

For tcsh shell:

````shell
setenv MAVEN3_HOME ~/local/apache-maven-3.0.4
setenv PATH ${MAVEN3}/bin:{PATH}
````

If you re-login into your shell and run the command

````shell
mvn -version
````

You should see reference to the MAVEN version you have just installed plus the JDK 7 that is using.

3. Get module from github
-------------------------

````shell
git clone https://github.com/opener-project/ner-base
````

4. Install module using maven
-----------------------------

````shell
cd ner-base/core
mvn clean package
````

This step will create a directory called target/ which contains various directories and files.
Most importantly, there you will find the module executable:

ehu-nerc-1.0.jar

This executable contains every dependency the module needs, so it is completely portable as long
as you have a JVM 1.7 installed.

To install the module in the local maven repository, usually located in ~/.m2/, execute:

````shell
mvn clean install
````

6. USAGE
--------

The program takes KAF documents (with <wf> and <term> elements) as standard input and outputs KAF.

To run the program execute:

````shell
cat wfterms.kaf | java -jar $PATH/target/ehu-nerc-$version.jar -l (de|en|es|fr|it|nl)
````

GENERATING JAVADOC
==================

You can also generate the javadoc of the module by executing:

````shell
mvn javadoc:jar
````

Which will create a jar file core/target/ehu-nerc-$version-javadoc.jar
