#!/bin/bash

./build.sh

if [ $TEST == "true" ]; then
  ./run.sh
fi