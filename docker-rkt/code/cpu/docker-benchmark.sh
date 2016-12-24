THREADS=$1

for i in {1..20} ; do 
	sudo docker run -it -v /home/cc/coremark_v1.0:/coremark dedocibula/container-benchmark-suite
	# sudo docker run dedocibula/container-benchmark-suite -it sysbench --test=cpu --cpu-max-prime=20000 --num-threads=$THREADS run >> sysbench-${THREADS}.txt
done
