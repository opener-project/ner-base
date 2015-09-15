
ixa-pipe-nerc
=============

ixa-pipe-nerc is a multilingual Sequence Labeler for tasks such as Named Entity
Recognition (NERC), Opinion Target Extraction (OTE) and SuperSense Tagging (SST).
**Current version is 1.5.2**

ixa-pipe-nerc is part of IXA pipes, a multilingual set of NLP tools developed
by the IXA NLP Group [http://ixa2.si.ehu.es/ixa-pipes].

Please go to [http://ixa2.si.ehu.es/ixa-pipes] for general information about the IXA
pipes tools but also for **official releases, including source code and binary
packages for all the tools in the IXA pipes toolkit**.

This document is intended to be the **usage guide of ixa-pipe-nerc**. If you really need to clone
and install this repository instead of using the releases provided in
[http://ixa2.si.ehu.es/ixa-pipes], please scroll down to the end of the document for
the [installation instructions](#installation).

**NOTICE!!**: ixa-pipe-nerc is now in [Maven Central](http://search.maven.org/)
for easy access to its API.

## TABLE OF CONTENTS

1. [Overview of ixa-pipe-nerc](#overview)
  + [Available features](#features)
  + [NERC distributed models](#nerc-models)
  + [OTE distributed models](#ote-models)
2. [Usage of ixa-pipe-nerc](#cli-usage)
  + [NERC tagging](#tagging)
  + [Opinion Target Extraction (OTE)](#ote)
  + [Training your own models](#training)
  + [Evaluation](#evaluation)
3. [API via Maven Dependency](#api)
4. [Git installation](#installation)

## OVERVIEW

ixa-pipe-nerc provides:

+ **NERC** for Basque, English, Spanish, Dutch, German and Italian. The named entity types are based on:
   + **CONLL**: LOCATION, MISC, ORGANIZATION and PERSON. See [CoNLL 2002](http://www.clips.ua.ac.be/conll2002/ner/)
   and [CoNLL 2003](http://www.clips.ua.ac.be/conll2003/ner/) for more information.
+ **Opinion Target Extraction** (OTE) for English. The models are trained on the SemEval 2014 and 2015 datasets;
  **ixa-pipe-nerc was the best system** in [SemEval 2015 OTE subtask within task 12](http://alt.qcri.org/semeval2015/task12/).
+ **SuperSense Tagging** (SST) for English. The models are trained on Semcor.

Every model is self-contained, that is, the prop files are not needed to use them.
You will find for each model a properties file describing its training although it is
not needed to run the model. Please see the traininParams.properties template file
for all available training options and documentation.

We provide competitive models based on robust local features and exploiting unlabeled data
via clustering features. The clustering features are based on Brown, Clark (2003)
and Word2Vec clustering plus some gazetteers in some cases.
To avoid duplication of efforts, we use and contribute to the API provided by the
[Apache OpenNLP project](http://opennlp.apache.org) with our own custom developed features for each of the three tasks.

### Features

**A description of every feature is provided in the trainParams.properties properties
file** distributed with ixa-pipe-nerc. As the training functionality is configured in
properties files, please do check this document. For each model distributed,
there is a prop file which describes the training of the model, as well as a
log file which provides details about the evaluation and training process.

### NERC-Models

Every result in reported here can be reproduced using the evaluation functionality of ixa-pipe-nerc or
with the [conlleval script](http://www.cnts.ua.ac.be/conll2002/ner/bin/conlleval.txt) using these scripts:

**Reproducing results with conlleval**: [conlleval-results](http://ixa2.si.ehu.es/ixa-pipes/models/results-conlleval.tar.gz)

**NERC models**:

  + **Release 1.5.0** [685MB]: [nerc-models-latest.tgz](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-models-1.5.0.tgz)
  + Release 1.4.0 [400MB+]: [nerc-models-1.4.0.tgz](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-models-1.4.0.tgz)
  + Releases 1.3.3-1.3.6: [nerc-models-1.3.+.tgz](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-models-1.3.3.tgz)

Every models is trained with the averaged Perceptron algorithm as described in (Collins 2002) and as implemented
in Apache OpenNLP.

+ **Basque**: eu-clusters model, trained on egunkaria dataset, F1 76.72 on 3 class evaluation and F1 75.40 on 4 classes.

+ **English Models**:

  + **CoNLL 2003 models**: We distribute models trained with local features
  and with external knowledge. Each of the models improve in F1 (reported on testb data)
  but they get somewhat slower:
    + CoNLL 2003 local + brown features: F1 88.50
    + CoNLL 2003 local + clark features: F1 88.97
    + CoNLL 2003 light clusters model: F1 90.27
    + CoNLL 2003 clusters model: F1 90.82
    + CoNLL 2003 clusters + dicts: F1 91.19

  + **Combined models**: trained using Ontonotes 4.0, conll03 and muc 7 data, good for out of domain usage.

+ **Spanish Models**:

  + CoNLL 2002 clusters: F1 84.16
  + CoNLL 2002 clusters + dict: F1 84.30

+ **Dutch Models**:
  + CoNLL 2002 clusters: F1 84.23
  + CoNLL 2002 clusters + dict: F1 84.91

+ **German Models**:
  + CoNLL 2003 clusters + dict: F1 76.42

+ **Italian Models**:
  + Evalita09 clusters: F1 80.38

### OTE-Models

+ **Latest models**: [ote-models-latest](http://ixa2.si.ehu.es/ixa-pipes/models/ote-models-1.5.0.tgz)

+ **English Models**:
    + Trained on SemEval 2014 restaurants dataset.
    + Trained on SemEval 2015 restaurants dataset (ote subtask winner).

## CLI-USAGE

ixa-pipe-nerc provides the following command-line basic functionalities:

1. **tag**: reads a NAF document containing *wf* and *term* elements and tags named
   entities.
2. **ote**: reads a NAF document containing *wf* and *term* elements and performs
   opinion target extraction (OTE).
3. **train**: trains new models for NERC, OTE and SST with several options
   available.
4. **eval**: evaluates a trained model with a given test set.
5. **cross**: it performs cross validation on a corpus.

Each of these functionalities are accessible by adding (tag|ote|train|eval|cross) as a
subcommand to ixa-pipe-nerc-$version.jar. Please read below and check the -help
parameter:

````shell
java -jar target/ixa-pipe-nerc-$version.jar (tag|ote|train|eval|cross) -help
````
**Every option for training is documented in the trainParams.properties file distributed with
ixa-pipe-nerc**. Please do read that file!!

### Tagging

If you are in hurry, just execute:

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar $PATH/target/ixa-pipe-nerc-$version.jar tag -m model.bin
````

If you want to know more, please follow reading.

ixa-pipe-nerc reads NAF documents (with *wf* and *term* elements) via standard input and outputs NAF
through standard output. The NAF format specification is here:

(http://wordpress.let.vupr.nl/naf/)

You can get the necessary input for ixa-pipe-nerc by piping
[ixa-pipe-tok](https://github.com/ixa-ehu/ixa-pipe-tok) and
[ixa-pipe-pos](https://github.com/ixa-ehu/ixa-pipe-pos) as shown in the
example.

There are several options to tag with ixa-pipe-nerc:

+ **model**: pass the model as a parameter.
+ **language**: pass the language as a parameter.
+ **outputFormat**: Output annotation in a format: available CoNLL03, CoNLL02,
  OpenNLP native format and NAF. It defaults to NAF.
+ **lexer**: switches on the rule-based DFA for NERC tagging. Currently we only provide
  one option **numeric**, which identifies "numeric entities" such as DATE,
  TIME, MONEY and PERCENT for all the languages currently in ixa-pipe-nerc.
+ **dictTag**: directly tag named entities contained in a gazetteer.
  + **tag**: with tag option, only dictionary entities are annotated.
  + **post**: with post option, the output of the statistical model is
    post-processed.
+ **dictPath**: the directory containing the gazetteers for the --dictTag
  option.

**Example**:

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar $PATH/target/ixa-pipe-nerc-$version.jar tag -m nerc-models-$version/en/en-local-conll03.bin
````
### OTE

As for NER tagging, the ote requires an input NAF with *wf* and *term* elements:

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar $PATH/target/ixa-pipe-nerc-$version.jar ote -m model.bin
````

ixa-pipe-nerc reads NAF documents (with *wf* and *term* elements) via standard input and outputs opinion targets in NAF
through standard output. The NAF format specification is here:

(http://wordpress.let.vupr.nl/naf/)

You can get the necessary input for ixa-pipe-nerc by piping
[ixa-pipe-tok](https://github.com/ixa-ehu/ixa-pipe-tok) and
[ixa-pipe-pos](https://github.com/ixa-ehu/ixa-pipe-pos) as shown in the
example.

There are several options to tag with ixa-pipe-nerc:

+ **model**: pass the model as a parameter.
+ **language**: pass the language as a parameter.
+ **outputFormat**: Output annotation in a format: available OpenNLP native format and NAF. It defaults to NAF.

**Example**:

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar $PATH/target/ixa-pipe-nerc-$version.jar ote -m ote-models-$version/en/ote-semeval2014-restaurants.bin
````

### Training

To train a new model for NERC, OTE or SST, you just need to pass a training parameters file as an
argument. As it has been already said, the options are documented in the
template trainParams.properties file.

**Example**:

````shell
java -jar target/ixa.pipe.nerc-$version.jar train -p trainParams.properties
````
**Training with Features using External Resources**: For training with dictionary or clustering
based features (Brown, Clark and Word2Vec) you need to pass the lexicon as
value of the respective feature in the prop file. This is only for training, as
for tagging or evaluation the model is serialized with all resources included.

### Evaluation

You can evaluate a trained model or a prediction data against a reference data
or testset.

+ **language**: provide the language.
+ **model**: if evaluating a model, pass the model.
+ **testset**: the testset or reference set.
+ **corpusFormat**: the format of the reference set and of the prediction set
  if --prediction option is chosen.
+ **prediction**: evaluate against a  prediction corpus instead of against a
  model.
+ **evalReport**: detail of the evaluation report
  + **brief**: just the F1, precision and recall scores
  + **detailed**, the F1, precision and recall per class
  + **error**: the list of false positives and negatives

**Example**:

````shell
java -jar target/ixa.pipe.nerc-$version.jar eval -m nerc-models-$version/en/en-local-conll03.bin -l en -t conll03.testb
````

## API

The easiest way to use ixa-pipe-nerc programatically is via Apache Maven. Add
this dependency to your pom.xml:

````shell
<dependency>
    <groupId>eus.ixa</groupId>
    <artifactId>ixa-pipe-nerc</artifactId>
    <version>1.5.2</version>
</dependency>
````

## JAVADOC

The javadoc of the module is located here:

````shell
ixa-pipe-nerc/target/ixa-pipe-nerc-$version-javadoc.jar
````

## Module contents

The contents of the module are the following:

    + formatter.xml           Apache OpenNLP code formatter for Eclipse SDK
    + pom.xml                 maven pom file which deals with everything related to compilation and execution of the module
    + src/                    java source code of the module and required resources
    + Furthermore, the installation process, as described in the README.md, will generate another directory:
    target/                 it contains binary executable and other directories
    + trainParams.prop      A template properties file containing documention
    for every available option


## INSTALLATION

Installing the ixa-pipe-nerc requires the following steps:

If you already have installed in your machine the Java 1.7+ and MAVEN 3, please go to step 3
directly. Otherwise, follow these steps:

### 1. Install JDK 1.7 or JDK 1.8

If you do not install JDK 1.7+ in a default location, you will probably need to configure the PATH in .bashrc or .bash_profile:

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

You should now see that your JDK is 1.7 or 1.8.

### 2. Install MAVEN 3

Download MAVEN 3 from

````shell
wget http://apache.rediris.es/maven/maven-3/3.0.5/binaries/apache-maven-3.0.5-bin.tar.gz
````
Now you need to configure the PATH. For Bash Shell:

````shell
export MAVEN_HOME=/home/ragerri/local/apache-maven-3.0.5
export PATH=${MAVEN_HOME}/bin:${PATH}
````

For tcsh shell:

````shell
setenv MAVEN3_HOME ~/local/apache-maven-3.0.5
setenv PATH ${MAVEN3}/bin:{PATH}
````

If you re-login into your shell and run the command

````shell
mvn -version
````

You should see reference to the MAVEN version you have just installed plus the JDK that is using.

### 3. Get module source code

If you must get the module source code from here do this:

````shell
git clone https://github.com/ixa-ehu/ixa-pipe-nerc
````

### 4. Compile

Execute this command to compile ixa-pipe-nerc:

````shell
cd ixa-pipe-nerc
mvn clean package
````
This step will create a directory called target/ which contains various directories and files.
Most importantly, there you will find the module executable:

ixa-pipe-nerc-$version.jar

This executable contains every dependency the module needs, so it is completely portable as long
as you have a JVM 1.7 installed.

To install the module in the local maven repository, usually located in ~/.m2/, execute:

````shell
mvn clean install
````

## Contact information

````shell
Rodrigo Agerri
IXA NLP Group
University of the Basque Country (UPV/EHU)
E-20018 Donostia-San Sebastián
rodrigo.agerri@ehu.eus
````
