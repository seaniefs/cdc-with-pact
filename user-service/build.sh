#!/bin/bash
mvn clean verify -Dpact.verifier.publishResults=true -Dpact.provider.version=1.0.0 -Dpact.provider.tag=DEV
