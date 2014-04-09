#!bin/bash

rm result2.txt 2>/dev/null
sudo iwlist scan 1> av_net 2> /dev/null

while read line
do
	echo $line > file1
	#grep "ESSID" file1 > another_file

	if grep -q "Address" file1; then
	  mac=`grep "Address" file1`
	  mac=`echo $mac`
	  mac=${mac#Cell*:}
	  printf "%s" "$mac" >> result2.txt
	fi
	
	if grep -q "Signal level" file1; then
          level=`grep "Signal level" file1`
	  level=`echo $level`
	  level=${level#*l:}
 	  level=${level%Noise*}
	  level=${level%dBm*}
	  printf " %s" "$level" >> result2.txt
	echo >> result2.txt
	# echo "---------------------------------" >> result2.txt
	fi
	
done < av_net

rm -f file1 av_net
