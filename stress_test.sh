#!/bin/bash

export desiredOutput="$(java -jar KPiler.jar test/test1_corrected.k test/test2_corrected.k test/test3_corrected.k test/test4_corrected.k test/test5_corrected.k test/test6_corrected.k)"

echo "Desired output:"
echo $desiredOutput

((errorCount = 0))
((matchCount = 0))

for number in {1..1000}
do
	export output="$(java -jar KPiler.jar test/test1_corrected.k test/test2_corrected.k test/test3_corrected.k test/test4_corrected.k test/test5_corrected.k test/test6_corrected.k)"

	if [ "$output" == "$desiredOutput" ]; then
  		echo "acerto"
		((matchCount++))
	else
		echo "error"
		((errorCount++))
	fi
done

echo "MATCHES $matchCount \n"
echo "ERRORS $errorCount \n"
exit 0
