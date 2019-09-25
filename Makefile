help:
	cat Makefile

clean:
	sbt clean
	yarn clean

fmt:
	sbt fmt

compile:
	sbt compile

test:
	sbt "testOnly * -- -oDF"

fast:
	sbt fastOptJS

fullOptJS:
	sbt fullOptJS

outdated:
	sbt dependencyUpdates

outdated-plugins:
	sbt ";dependencyUpdates; reload plugins; dependencyUpdates"

evicted:
	sbt evicted

prepare:
	yarn install

destroy:
	rm -rf ./node_modules

.PHONY: build build-opt
build: clean fast

build-opt: clean fullOptJS

parcel: build
	yarn build-fast

parcel-opt: build-opt
	yarn build-opt

wepack-fast: build
	yarn webpack-build-fast

stage: fullOptJS
	yarn stage

deploy-gcp:
	gcloud functions deploy cloudBuildSlackNotifications \
		--entry-point entryPoint \
		--trigger-topic cloud-builds \
		--runtime nodejs10 \
		--source ./target/dist

deploy: clean stage deploy-gcp
