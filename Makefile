help:
	cat Makefile

clean:
	sbt clean
	yarn clean

fmt:
	sbt fmt

lint:
	sbt lint

compile:
	sbt compile

test:
	sbt lint "testOnly * -- -oDF"

coverage:
	sbt cov

fast:
	sbt fastOptJS

fullOptJS:
	sbt fullOptJS

outdated: outdated-scala outdated-sbt-plugins outdated-node

outdated-scala:
	sbt dependencyUpdates

outdated-sbt-plugins:
	sbt ";dependencyUpdates; reload plugins; dependencyUpdates"

outdated-node:
	yarn outdated

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
		--region asia-northeast1 \
		--entry-point entryPoint \
		--trigger-topic cloud-builds \
		--runtime nodejs10 \
		--source ./target/dist

deploy: clean stage deploy-gcp

submit-cloud-build:
	cd docker && \
		gcloud builds submit . --config=cloudbuild.yaml
