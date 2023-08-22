docker-pull:
	docker pull maven:3.8.6-openjdk-11-slim

.PHONY: build-image
build-image: docker-pull \
             $(call print-help,build, "Build the docker image.")
	docker build -t reactome/data-export:latest .

.PHONY: run-image
run-image: $(call print-help,run, "Run the docker image.")
	docker run reactome/data-export:latest -v $(pwd)/output:/graphdb --net=host
