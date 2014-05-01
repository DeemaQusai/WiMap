#!bin/bash

rm result.txt 2>/dev/null
sudo iwlist scan > result.txt
