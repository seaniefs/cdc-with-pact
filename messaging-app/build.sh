#!/bin/bash
mvn clean verify
mvn pact:publish
