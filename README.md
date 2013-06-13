# NER Base

This repository contains the source code used for performing Named Entity
Recognition for the following languages:

* Dutch
* English
* German
* Italian
* Spanish

## Requirements

* Java 1.7 or newer
* Ruby 1.9.2 or newer

Development requirements:

* Maven
* Bundler

## Installation

Installing as a regular Gem:

    gem install opener-ner-base

Using Bundler:

    gem 'opener-ner-base',
      :git    => 'git@github.com:opener-project/ner-base.git',
      :branch => 'master'

Using specific install:

    gem install specific_install
    gem specific_install opener-ner-base \
       -l https://github.com/opener-project/ner-base.git

## Usage

    cat some_input_file.kaf | ner-en

## Contributing

First make sure all the required dependencies are installed:

    bundle install

Then compile the required Java code:

    bundle exec rake java:compile

For this you'll need to have Java 1.7 and Maven installed. These requirements
are verified for you before the Rake task calls Maven.

## Testing

To run the tests (which are powered by Cucumber), simply run the following:

    bundle exec rake

This will take care of verifying the requirements, installing the required Java
packages and running the tests.

For more information on the available Rake tasks run the following:

    bundle exec rake -T

## Structure

This repository comes in two parts: a collection of Java source files and Ruby
source files. The Java code can be found in the `core/` directory, everything
else will be Ruby source code.
