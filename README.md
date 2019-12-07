# cloud-build-slack-notifications

[![](https://github.com/grimrose/cloud-build-slack-notifications/workflows/CI/badge.svg)](https://github.com/grimrose/cloud-build-slack-notifications/actions)

## requirement

- sbt
- node.js
    - 10.15.3
    - ref: https://cloud.google.com/functions/docs/concepts/exec#runtimes
- yarn
- gcloud

### development

- [jakedeichert/mask](https://github.com/jakedeichert/mask)
- docker
- docker-compose

## usage

[tasks](maskfile.md)

### setup

```shell script
mask setup
```

### test

```shell script
mask test
```

### full build

```shell script
mask full
```

## for update

```shell script
mask outdated
```

check [oyvindberg/ScalablyTyped](https://github.com/oyvindberg/ScalablyTyped) release version
