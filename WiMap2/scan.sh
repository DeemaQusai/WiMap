#!bin/bash

rm result.txt 2>/dev/null
sudo iwlist scan 1> av_net 2> /dev/null

while read line
do
	echo $line > file1
	#grep "ESSID" file1 > another_file

	if grep -q "Address" file1; then
	  mac=`grep "Address" file1`
	  mac=`echo $mac`
	  mac=${mac#Cell*-}
	  echo $mac >> result.txt
	fi

	if grep -q "ESSID" file1; then
	  echo $line >> result.txt
	fi
	
	if grep -q "Signal level" file1; then
          level=`grep "Signal level" file1`
	  level=`echo $level`
	  level=${level#*l:}
 	  level=${level%Noise*}
	  level=${level%dBm*}
	  printf "RSSI: %s\n" "$level" >> result.txt
#	  echo $level >> result.txt
	  echo "---------------------------------" >> result.txt
	fi
done < av_net

rm -f file1 av_net


