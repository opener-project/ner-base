# Opener::Kernel::EHU::NER::Lite::EN

## Initial Version

TODO: Write a gem description

## Installation

Add this line to your application's Gemfile:

    gem 'EHU-ner-lite_EN_kernel', :git=>"git@github.com/opener-project/EHU-ner-lite_EN_kernel.git"

And then execute:

    $ bundle install

Or install it yourself as:

    $ gem specific_install EHU-ner-lite_EN_kernel -l https://github.com/opener-project/EHU-ner-lite_EN_kernel.git


If you dont have specific_install already:

    $ gem install specific_install

## Usage

Once installed as a gem you can access the gem from anywhere:


TODO: Change output below as needed
````shell
echo "foo" | EHU-ner-lite_EN_kernel
````

Will output

````
oof
````

## Contributing

1. Pull it
2. Create your feature branch (`git checkout -b features/my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin features/my-new-feature`)
5. If you're confident, merge your changes into master.

IXA EHU-OPENNLP-NERC-EN module
==================

This module provides a simple wrapper that uses Apache OpenNLP
programatically to recognize and classify Named Entities (NERC)
in running text.

The NERC model have been trained using the English CoNLL 2003 data and are provided
by the IXA NLP Group (ixa.si.ehu.es), University of the Basque Country (F1 84.80).

Version 1.0 of ehu-opennlp-nerc-en is being developed as part of the 7th Framework OpeNER European Project.


Contents
========

The contents of the module are the following:

- core: directory containing the NERC module

    + formatter.xml           Apache OpenNLP code formatter for Eclipse SDK
    + INSTALL                 Instructions to install and run the module
    + opener/                 trained models for sentence segmentation, tokenization and NERC in English
    + pom.xml                 maven pom file which deals with everything related to compilation and execution of the module
    + src/                    java source code of the module
    + Furthermore, the installation process, as described in the README.md, will generate another directory:
    target/                 it contains binary executable and other directories

- features: directory containing test data
- README.md: This README


INSTALLING Version 1.0 of ehu-opennlp-nerc-en module
=======================================================

Installing the nerc-en module requires the following steps:

If you already have installed in your machine JDK6 and MAVEN 3, please go to step 3
directly. Otherwise, follow these steps:

1. Install JDK 1.6
-------------------

If you do not install JDK 1.6 in a default location, you will probably need to configure the PATH in .bashrc or .bash_profile:

````shell
export JAVA_HOME=/yourpath/local/java6
export PATH=${JAVA_HOME}/bin:${PATH}
````

If you use tcsh you will need to specify it in your .login as follows:

````shell
setenv JAVA_HOME /usr/java/java16
setenv PATH ${JAVA_HOME}/bin:${PATH}
````

If you re-login into your shell and run the command

````shell
java -version
````

You should now see that your jdk is 1.6

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
git clone git@github.com:opener-project/EHU-ner-lite_EN_kernel.git
````

4. Move into core directory
---------------------------

````shell
cd EHU-ner-lite_EN_kernel/core
````

5. Install module using maven
-----------------------------

````shell
mvn clean install
````

This step will create a directory called target/ which contains various directories and files.
Most importantly, there you will find the module executable:

ehu-opennlp-nerc-en-1.0.jar

This executable contains every dependency the module needs, so it is completely portable as long
as you have a JVM 1.6 installed.

The program takes KAF documents (with <wf> and <term> elements) as standard input and outputs KAF.

To run the program execute:

````shell
cat wfterms.kaf | java -jar $PATH/target/ehu-opennlp-nerc-en-1.0.jar
````

GENERATING JAVADOC
==================

You can also generate the javadoc of the module by executing:

````shell
mvn javadoc:jar
````

Which will create a jar file core/target/ehu-opennlp-nerc-en-1.0-javadoc.jar



Contact information
===================

````shell
Rodrigo Agerri
IXA NLP Group
University of the Basque Country (UPV/EHU)
E-20018 Donostia-San Sebastián
rodrigo.agerri@ehu.es
````








