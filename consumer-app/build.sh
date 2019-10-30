#!/bin/bash

function doBuild {
  mvn $1 $2 -Dpactbroker.auth.username=pacttest -Dpactbroker.auth.password=pacttest -Dpactbroker.scheme=http -Dpactbroker.host=127.0.0.1 -Dpactbroker.port=9292
  return $?
}

doBuild clean verify
if [[ "$?" == "0" ]]; then
  doBuild pact:publish
fi
