#!/bin/bash
docker run -it -p 127.0.0.1:9292:9292/tcp -e PACT_BROKER_DATABASE_ADAPTER=sqlite -e PACT_BROKER_DATABASE_NAME=pactbroker.sqlite -e PACT_BROKER_BASIC_AUTH_USERNAME=pacttest -e PACT_BROKER_BASIC_AUTH_PASSWORD=pacttest pactfoundation/pact-broker
