THREADS=$1

for i in {1..20} ; do 
	sudo rkt --insecure-options=image --volume volume-coremark,kind=host,source=/home/cc/coremark_v1.0,readOnly=false run dedocibula-container-benchmark-suite-latest.aci
	# sudo rkt --insecure-options=image run dedocibula-container-benchmark-suite-latest.aci --interactive=true --exec sysbench -- --test=cpu --cpu-max-prime=20000 --num-threads=$THREADS run >> rkt-sysbench-${THREADS}.txt
done
