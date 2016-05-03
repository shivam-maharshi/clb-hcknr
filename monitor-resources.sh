sudo top -b -d 60 | grep --line-buffered "Mem*\|Cpu*\|Swap*" > ~/top.txt
