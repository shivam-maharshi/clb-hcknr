import sys

def main():
	datasetfile = sys.argv[1]
	parts = int(sys.argv[2])

	with open(datasetfile) as f:
		lines = f.readlines()

	for i in range(0, parts):
		with open("split-dataset-{}.txt".format(i), "a") as output:
			chunk = len(lines) / parts
			for j in range(chunk * i, chunk * (i + 1)):
				if j == chunk * (i + 1) - 1 and lines[j][-1] == '\n':
					output.write(lines[j][:-1])
				else:
					output.write(lines[j])

if __name__ == "__main__": main()