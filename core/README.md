
ixa-pipe-nerc
=============

ixa-pipe-nerc is multilingual Named Entity Recognition and Classification tagger. 

This document is intended to be the **usage guide of ixa-pipe-nerc**. If you really need to clone
and install this repository please scroll down to the end of the document for
the [installation instructions](#installation).

## TABLE OF CONTENTS

1. [Overview of ixa-pipe-nerc](#overview)
  + [List of distributed models](#models)
2. [Usage of ixa-pipe-nerc](#usage)
  + [NERC tagging](#tagging)
  + [Training your own models](#training)
  + [Evaluation](#evaluation)

## OVERVIEW

ixa-pipe-nerc provides NERC English, Spanish, Dutch, German and Italian. The named entity types are based on:

+ **CONLL**: LOCATION, MISC, ORGANIZATION and PERSON. See [CoNLL 2002](http://www.clips.ua.ac.be/conll2002/ner/)
and [CoNLL 2003](http://www.clips.ua.ac.be/conll2003/ner/) for more information. 
+ **ONTONOTES 4.0**: 18 Named Entity types: TIME, LAW, GPE, NORP, LANGUAGE,
PERCENT, FACILITY, PRODUCT, ORDINAL, LOCATION, PERSON, WORK_OF_ART, MONEY, DATE, EVENT, QUANTITY, ORGANIZATION, CARDINAL.

We currently provide two very fast language independent featuresets and one
featureset more accurate but considerably slower. The language independent
features are based on the features presented by Zhang and Johnson (2003) with several differences: We do not use POS
tags, chunking or gazetteers in our baseline models but we do use
bigrams as a feature. To avoid duplication of efforts, we use the machine
learning API provided by the [Apache OpenNLP project](http://opennlp.apache.org).

**We distribute the ixa-pipe-nerc resources in two packages**: 

  + The [nerc-default-resources.tgz](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-default-resources.tgz) which contains the **required default 
  resources for ixa-pipe-nerc to compile and run**. This package is distributed in the
  releases of the ixa pipes tools. 
  + The [nerc-resources.tgz](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-resources.tgz) package, which contains **every model and resource** available.

### Models

Several models per dataset and per language depending on the featureset used are available: 

+ **opennlp features**: it implements the default features as available in the Apache
  OpenNLP API. We implement this featureset to make available models that are
  fully compatible with the Apache OpenNLP distribution. Note that the models
  using other features different to the opennlp featureset will not perform
  well in Apache OpenNLP CLI. 
+ **baseline features**: it implements local, language independent features. These
  features generate reasonably accurate and very fast models.
+ **dictionary features**: baseline features with additional dictionary-based features as
  implemented by Ratinov and Roth (2009). Design Challenges and Misconceptions in 
  Named Entity Recognition. In CoNLL. These models are more accurate but
  slower than the opennlp and baseline models; you need dictionaries formatted:
  Named Entity Token\tabNamed Entity class\newline. It is possible to obtain these 
  dictionaries from Ratinov and Roth(2009) but **only for English**.

* **English Models**: we offer a variety of Perceptron based models (Collins 2002):
  
  + **CoNLL 2003 models** trained on train and dev sets and evaluated on test set.
    + CoNLL opennlp features: **en-nerc-perceptron-opennlp-c0-b3-testa.bin**: F1 83.80
    + CoNLL baseline features: **en-nerc-perceptron-baseline-c0-b3.testa.bin**: F1 84.53
    + CoNLL dict features: **en-nerc-perceptron-dict-c0-b3-conll03-testa.bin**: F1 87.11
 
 + **Ontonotes 4.0**: 
    + Trained on the **full corpus** with the **18 NE types**, suitable **for production use**.
      + Ontonotes 18 NE types opennlp features: **en-nerc-perceptron-opennlp-c0-b3-ontonotes-4.0.bin**
      + Ontonotes 18 NE types baseline features: **en-nerc-perceptron-baseline-c0-b3-ontonotes-4.0.bin**
    + **Using 5K sentences at random for testset** from the corpus and leaving the rest (90K
      aprox) for training.
      + Ontonotes CoNLL 4 NE types opennlp features: **en-nerc-perceptron-ontonotes-4.0-4-types-opennlp-c0-b3.bin**: F1 86.04
      + Ontonotes 3 NE types opennlp features: **en-nerc-perceptron-ontonotes-4.0-3-types-opennlp-c0-b3.bin**: F1 88.83
      + Ontonotes CoNLL 4 NE types baseline features: **en-nerc-perceptron-ontonotes-4.0-4-types-baseline-c0-b3.bin**: F1 86.21
      + Ontonotes 3 NE types baseline features: **en-nerc-perceptron-ontonotes-4.0-3-types-baseline-c0-b3.bin**: F1 89.41

+ **English multi corpora models**: We also provide 4 models trained with a mixture of Ontonotes and CoNLL datasets, with the aim of improving the models robustness across domains.  The models are trained using the *full Ontonotes* corpus and the *train and dev* sets of the CoNLL 2003 corpus. The testset was prepared by taking 8K sentences at random leaving the rest for training (103K sentences). We use both *baseline* and *opennlp* featuresets (models trained with the *opennlp* features are directly usable with the Apache OpenNLP CLI).
  + **CoNLL 4 NE types opennlp features**: en-nerc-perceptron-conll03-ontonotes-4.0-4-types-opennlp-c0-b3.bin: F1 86.63
  + **3 NE types opennlp features**: en-nerc-perceptron-conll03-ontonotes-4.0-3-types-opennlp-c0-b3.bin: F1 88.11
  + **CoNLL 4 NE types baseline features**: en-nerc-perceptron-conll03-ontonotes-4.0-4-types-baseline-c0-b3.bin: F1 87.36
  + **3 NE types baseline features**: en-nerc-perceptron-conll03-ontonotes-4.0-3-types-baseline-c0-b3.bin: 88.95

+ **Spanish Models**: we obtained better results overall with Maximum Entropy
  models (Ratnapharki 1999). The best results are obtained when a c0 (cutoff 0)
  is used, but those models are slower for production than when a c4 (cutoff 4)
  is used. Therefore, we provide both types for opennlp and baseline features: 

  + CoNLL **es-nerc-maxent-opennlp-750-c0-b3-testa.bin**: F1 80.01
  + CoNLL **es-nerc-maxent-opennlp-750-c4-b3-testa.bin**: F1 77.85
  + CoNLL **es-nerc-maxent-baseline-750-c0-b3-testa.bin**: F1 80.25
  + CoNLL **es-nerc-maxent-baseline-750-c4-b3-testa.bin**: F1 79.73

+ **Dutch Models**: We distribute the following CoNLL02 models:
  + **nl-nerc-perceptron-baseline-c0-b3-conll02-testa.bin**: F1 77.57
  + **nl-nerc-perceptron-opennlp-c0-b3-conll02-testa.bin**: F1 75.92

+ **German Models**: We distribute the following CoNLL02 models:
  + **de-nerc-perceptron-baseline-c0-b3-conll03-testa.bin**: F1 69.02
  + **de-nerc-perceptron-opennlp-c0-b3-conll03-testa.bin**: F1 61.48

+ **Italian Models**: Currently we distribute models trained with Evalita07 and Evalita09: 
  + Evalita07 **it-nerc-perceptron-baseline-c0-b3-evalita07.bin**: F1 70.74
  + Evalita07 **it-nerc-perceptron-opennlp-c0-b3-evalita07.bin**: F1 65.92
  + Evalita09 **it-nerc-perceptron-baseline-c0-b3-evalita09.bin**: F1 74.38
  + Evalita09 **it-nerc-perceptron-opennlp-c0-b3-evalita07.bin**: F1 72.89

**Summarizing**, and even though the best way of knowing which model to use is
to try them, for production use, we recommend using: 
  + English 18 NE types ontonotes baseline model: **en-nerc-perceptron-baseline-c0-b3-ontonotes-4.0.bin**
  + English CoNLL 4 NE types multi corpora baseline model: **en-nerc-perceptron-conll03-ontonotes-4.0-4-types-baseline-c0-b3.bin**
  + English 3 NE types multicorpora baseline model: **en-nerc-perceptron-conll03-ontonotes-4.0-3-types-baseline-c0-b3.bin**
  + Spanish CoNLL 4 NE types baseline c4 model: **es-nerc-maxent-baseline-750-c4-b3-testa.bin**.

The default models provided in the package [nerc-default-resources.tgz](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-default-resources.tgz) are: 
  + English CoNLL 4 NE types multi corpora baseline model: **en-nerc-perceptron-conll03-ontonotes-4.0-4-types-baseline-c0-b3.bin**
  + Spanish baseline c4 model: **es-nerc-maxent-baseline-750-c4-b3-conll02-testa.bin**
  + German baseline CoNLL03 4 types model: **de-nerc-perceptron-baseline-c0-b3-conll03-testa.bin**
  + Dutch baseline CoNLL02 4 types model: **nl-nerc-perceptron-baseline-c0-b3-conll02-testa.bin**
  + Italian baseline Evalita09 4 types model: **nl-nerc-perceptron-baseline-c0-b3-evalita09.bin**

## USAGE

ixa-pipe-nerc provides 3 basic functionalities:

1. **tag**: reads a NAF document containing *wf* and *term* elements and tags named
   entities.
2. **train**: trains new model for English or Spanish with several options
   available.
3. **eval**: evaluates a trained model with a given test set.

Each of these functionalities are accessible by adding (tag|train|eval) as a
subcommand to ixa-pipe-nerc-$version.jar. Please read below and check the -help
parameter: 

````shell
java -jar target/ixa-pipe-nerc-$version.jar (tag|train|eval) -help
````

### Tagging 

If you are in hurry, just execute: 

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar $PATH/target/ixa-pipe-nerc-$version.jar tag
````

If you want to know more, please follow reading.

ixa-pipe-nerc reads NAF documents (with *wf* and *term* elements) via standard input and outputs NAF
through standard output. The NAF format specification is here:

(http://wordpress.let.vupr.nl/naf/)

You can get the necessary input for ixa-pipe-nerc by piping 
[ixa-pipe-tok](https://github.com/ixa-ehu/ixa-pipe-tok) and 
[ixa-pipe-pos](https://github.com/ixa-ehu/ixa-pipe-pos) as shown in the
example. 

There are several options to tag with ixa-pipe-nerc which can be combined
together:

+ **lang**: choose between en and es. If no language is chosen, the one specified
  in the NAF header will be used.
+ **features**: choose features to use during the decoding. Currently 3 feature
  types are provided: 
  + **opennlp**: it implements the default features as available in the Apache
     OpenNLP API.
  + **baseline**: it implements local, language independent features. These
     features generate reasonably accurate and very fast models.
  + **dict**: baseline features with additional dictionary-based features as
     implemented by Ratinov and Roth (2009). Design Challenges and Misconceptions in 
     Named Entity Recognition. In CoNLL. These models are more accurate but
     much slower than the opennlp and baseline models. It is **required** to
     provide --dictPath to use these features, for tagging, training and
     evaluation.
+ **model**: provide the model to do the tagging. If no model is provided via
  this parameter, ixa-pipe-nerc will revert to the CoNLL baseline model distributed
  in the nerc-default-resources.tgz. 
+ **beamsize**: choose beam size for decoding. There is no definitive evidence
  that using larger or smaller beamsize actually improves accuracy. It is known
  to slow things down considerably if beamsize is set to 100, for example.
+ **dictionaries**: two mutually exclusive options are available.
  + **post**: post-process the probabilistic tagging by dictionary look up and
  correct/add those NE classes deemed to be incorrect by the dictionary.
  + **tag**: tag only those entities that appear in the dictionaries. This
    option requires the --dictPath option below.
+ **dictPath**: provide the directory path containing the dictionaries to be
  used with the **dictionaries** option and with the -f dict features.
+ **lexer**: switches on the rule-based DFA for NERC tagging. Currently we only provide
  one option **numeric**, which identifies "numeric entities" such as DATE,
  TIME, MONEY and PERCENT for all the languages currently in ixa-pipe-nerc.

**Example**: 

````shell
cat file.txt | ixa-pipe-tok | ixa-pipe-pos | java -jar $PATH/target/ixa-pipe-nerc-$version.jar tag
````

### Training

The following options are available via the train subcommand:

+ **features**: as explained in the previous section. Obviously, for best
  performance the features used at training should be used for tagging.
+ **input**: the training dataset.
+ **testSet**: self-explanatory, the test dataset.
+ **devSet**: the development set if cross evaluation is chosen to find the
  right number of iterations (this option is still very experimental).
+ **output**: the model name resulting of the training. If not output is
  chosen, ixa-pipe-nerc will save the model in a file named following the
  features used.
+ **params**: this is where most of the training options are specified.
  + **Algorithm**: choose between PERCEPTRON or MAXENT.
  + **Iterations**: choose number of iterations.
  + **Cutoff**: consider only events above the cutoff number specified.
  + **Threads**: multi-threading, only works with MAXENT.
  + **Language**: en or es.
  + **Types**: choose the Named Entity types to do the training.
  + **Beamsize**: choose beamsize for decoding. It defaults to 3.
  + **Corpus**: corpus format. Currently opennlp native format or CoNLL 2003
    are accepted.
  + **CrossEval**: choose the range of iterations at which to perform
  evaluation. This parameter tells the trainer to find the best number of
  iterations for MAXENT training on a development set. Then that iteration
  number will be used to train the final model. In a very experimental state. 

**Example**:

````shell
java -jar target/ixa.pipe.nerc-1.0.jar train -f baseline -p trainParams.txt -i train.data -t test.data -o test-nerc.bin
````

### Evaluation

To evaluate a trained model, the eval subcommand provides the following
options: 

+ **model**: input the name of the model to evaluate.
+ **features**: as explained for *train* and *tag* subcommands.
+ **language**: input en or es.
+ **testSet**: testset to evaluate the model.
+ **evalReport**: choose the detail in displaying the results: 
  + **brief**: just the Precision, Recall and F scores.
  + **detailed**: scores per NE class. 
  + **error**: print to stdout all the false positives.
+ **corpus**: choose between native opennlp and conll 2003 formats.
+ **beamsize**: choose beamsize for decoding.
+ **netypes**: choose the Named Entity types to do the evaluation.
**Example**:

````shell
java -jar target/ixa.pipe.nerc-$version.jar eval -m test-nerc.bin -f baseline -l en -t test.data -c conll
````

## JAVADOC

It is possible to generate the javadoc of the module by executing:

````shell
cd ixa-pipe-nerc/
mvn javadoc:jar
````

Which will create a jar file core/target/ixa-pipe-nerc-$version-javadoc.jar

## Module contents

The contents of the module are the following:

    + formatter.xml           Apache OpenNLP code formatter for Eclipse SDK
    + pom.xml                 maven pom file which deals with everything related to compilation and execution of the module
    + src/                    java source code of the module and required resources
    + Furthermore, the installation process, as described in the README.md, will generate another directory:
    target/                 it contains binary executable and other directories


## INSTALLATION

Installing the ixa-pipe-nerc requires the following steps:

If you already have installed in your machine the Java 1.7+ and MAVEN 3, please go to step 3
directly. Otherwise, follow these steps:

### 1. Install JDK 1.7

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

You should now see that your JDK is 1.7

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

You should see reference to the MAVEN version you have just installed plus the JDK 7 that is using.

### 3. Get module source code

If you must get the module source code from here do this:

````shell
git clone https://github.com/ixa-ehu/ixa-pipe-nerc
````

### 4. Download the Resources

You will need to download the trained models and other resources and copy them to ixa-pipe-nerc/src/main/resources/
for the module to work properly:

Download the [default models](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-default-resources.tgz) and untar the archive into the src/main/resources directory:

````shell
cd ixa-pipe-nerc/src/main/resources
wget http://ixa2.si.ehu.es/ixa-pipes/models/nerc-default-resources.tgz
tar xvzf nerc-default-resources.tgz
````
The nerc-default-resources package contains the baseline models to which ixa-pipe-nerc backs off if not model is provided as parameter
for tagging. If you require other models, as explained in the [overview](#overview), please additionally download the [nerc-resources](http://ixa2.si.ehu.es/ixa-pipes/models/nerc-resources.tgz) package. 

### 5. Compile

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
rodrigo.agerri@ehu.es
````
