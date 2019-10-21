# Tasks

> Development tasks for project

## setup

> Setup for development

~~~sh
yarn install
~~~

## destroy

> Remove node_modules

~~~sh
rm -rf ./node_modules
~~~

## clean

> Clean dist files

~~~sh
yarn clean
sbt clean
~~~

## lint

> Linting scala and sbt files

~~~sh
sbt lint
~~~

## fmt

> Formatting scala and sbt files

~~~sh
sbt fmt
~~~

## test

> Run test and lint project files

~~~sh
sbt lint test
~~~

### test only

> Run test only specific case

**OPTIONS**
* test_case
    * flags: --case
    * type: string
    * desc: specific test class name

~~~sh
TEST_CASE=${test_case:-*}
sbt "testOnly $TEST_CASE -- -oDF"
~~~

## coverage

~~~sh
sbt cov
~~~

## outdated

> Show outdated modules

~~~sh
mask outdated sbt
mask outdated node
~~~

### outdated scala

~~~sh
sbt dependencyUpdates
~~~

### outdated sbt

~~~sh
sbt "dependencyUpdates; reload plugins; dependencyUpdates"
~~~

### outdated node

~~~sh
yarn outdated
~~~

## evicted

> Show evicted modules

~~~sh
sbt evicted
~~~

## compile

> Compile scala

~~~sh
sbt compile
~~~

## fast

> Compile fast optimization

~~~sh
sbt fastOptJS
~~~

## full

> Compile full optimization

~~~sh
sbt fullOptJS
~~~

## parcel

~~~sh
mask fast
yarn stage-parcel-fast
~~~

### parcel opt

~~~sh
mask full
yarn stage-parcel-opt
~~~

## stage

> Stage files for deployment

~~~sh
mask full
yarn stage
~~~

## gcloud 

> Show gcloud sdk version

~~~sh
gcloud version
~~~

### gcloud builds-submit

> gcloud builds submit

~~~sh
cd docker
echo $(pwd)
gcloud builds submit . --config=cloudbuild.yaml
~~~

### gcloud configurations

~~~sh
gcloud config configurations list
~~~

### gcloud functions-events

~~~sh
gcloud functions event-types list
~~~

### gcloud functions-logs

~~~sh
gcloud functions logs read cloudBuildSlackNotifications
~~~

### gcloud functions-deploy

~~~sh
gcloud functions deploy cloudBuildSlackNotifications \
    --region asia-northeast1 \
    --entry-point entryPoint \
    --trigger-topic cloud-builds \
    --runtime nodejs10 \
    --source ./target/dist
~~~

## deploy

> Deploy Cloud Functions

~~~sh
mask stage
mask gcloud functions-deploy
~~~

## hello-world

~~~sh
mask hello-world help
~~~

### hello-world start

~~~sh
mask fast && \
yarn start-hello-world
~~~

### hello-world stage

~~~sh
mask full && \
yarn stage-hello-world
~~~

### hello-world deploy

~~~sh
mask hello-world stage && \
gcloud functions deploy helloWorld \
    --region asia-northeast1 \
    --entry-point helloWorld \
    --trigger-http \
    --set-env-vars NODE_ENV=production \
    --runtime nodejs10 \
    --source ./target/dist
~~~

### hello-world logs

~~~sh
gcloud functions logs read helloWorld
~~~
