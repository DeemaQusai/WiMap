#!bin/bash

rm result.txt 2>/dev/null
sudo iwlist scan 1> av_net 2> /dev/null

while read line
do
	echo $line >> result.txt
done < av_net

rm -f file1 av_net


