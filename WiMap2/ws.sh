#This is a bash shell script that takes 5 readings of the RSSI (dBm) for 5 seconds and
#computes the average

#!/bin/bash


rm signalLevel.txt 2> /dev/null
  Sum=0
  i=0
  while [ $i -lt 10 ]
  do
     k=`sed -n '3p' /proc/net/wireless | awk '{split($4,a,"."); print a[1]}'`
#    echo $k
     Sum=`expr $Sum + $k`
     i=`expr $i + 1`
     sleep 0.25
  done

#  printf "Sum = "
#  echo $Sum
  avg=`echo "$Sum / 10.0" | bc -l`
#  printf "Avg = "
  echo $avg > signalLevel.txt

#  j=`expr $j + 1`
#  sleep 1

#done
