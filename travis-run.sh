#!/bin/bash

./make.sh

if [ $TEST == "true" ]; then
  ./run.sh
fi