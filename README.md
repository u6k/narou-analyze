# narou-analyze

[![Travis](https://img.shields.io/travis/u6k/narou-analyze.svg)](https://travis-ci.org/u6k/narou-analyze)
[![GitHub release](https://img.shields.io/github/release/u6k/narou-analyze.svg)](https://github.com/u6k/narou-analyze/releases)
[![license](https://img.shields.io/github/license/u6k/narou-analyze.svg)](https://github.com/u6k/narou-analyze/blob/master/LICENSE)
[![Docker Pulls](https://img.shields.io/docker/pulls/u6kapps/narou-analyze.svg)](https://hub.docker.com/r/u6kapps/narou-analyze/)

「小説家になろう」をクローリングして、データを収集、分析します。

## Requirement

- Docker

```
$ docker version
Client:
 Version:      1.12.0
 API version:  1.24
 Go version:   go1.6.3
 Git commit:   8eab29e
 Built:        Thu Jul 28 23:54:00 2016
 OS/Arch:      windows/amd64

Server:
 Version:      17.03.0-ce
 API version:  1.26
 Go version:   go1.7.5
 Git commit:   3a232c8
 Built:        Tue Feb 28 07:52:04 2017
 OS/Arch:      linux/amd64
```

## Usage

### 検索ページから全小説のURLを取得

```
curl -v \
    -X POST \
    -H "Content-Type: application/json" \
    -d '{"searchDate":"2017-04-10"}' \
    https://crawler.narou-analyze.u6k.me/api/novels/
```

- searchDate
    - 「小説家になろう」検索画面で入力する検索日付
    - 指定日付に更新された小説をインデックスする

### 小説のメタデータをダウンロード

```
curl -v \
    -X POST \
    https://crawler.narou-analyze.u6k.me/api/novels/n1234ab/meta/download
```

### 小説のメタデータを解析

```
curl -v \
    -X POST \
    https://crawler.narou-analyze.u6k.me/api/novels/n1234ab/meta/
```

### 小説の内容をダウンロード

```
curl -v \
    -X POST \
    https://crawler.narou-analyze.u6k.me/api/novels/n1234ab/content/download
```

### 小説の内容を解析

```
curl -v \
    -X POST \
    https://crawler.narou-analyze.u6k.me/api/novels/n1234ab/content/
```

## Installation

### 開発環境を構築

開発用Dockerイメージをビルドします。

```
docker build -t narou-analyze-dev -f Dockerfile-dev .
```

Eclipseプロジェクトを作成します。

```
docker run \
    --rm \
    -v "${HOME}/.m2:/root/.m2" \
    -v "${PWD}:/var/my-app" \
    narou-analyze-dev mvn eclipse:eclipse
```

### 開発環境でアプリケーションを実行

PostgreSQLコンテナを起動します。

```
docker run \
    -d \
    --name db \
    -e "POSTGRES_PASSWORD=db_pass" \
    -e "POSTGRES_USER=db_user" \
    -e "POSTGRES_DB=narou_analyze" \
    postgres
```

開発用コンテナを起動します。

```
docker run \
    --rm \
    --name narou_analyze \
    -v "${HOME}/.m2:/root/.m2" \
    -v "${PWD}:/var/my-app" \
    --link db:db \
    -e "NAROU_CRAWLER_DB_USER=db_user" \
    -e "NAROU_CRAWLER_DB_PASS=db_pass" \
    -e "NAROU_CRAWLER_DB_HOST=db" \
    -e "NAROU_CRAWLER_DB_NAME=narou_analyze" \
    -p 8080:8080 \
    narou-analyze-dev mvn spring-boot:run
```

### ビルド

PostgreSQLコンテナを起動します。コマンドは上記と同じ。

テスト実行し、jarファイルをパッケージングします。

```
docker run \
    --rm \
    --name narou_analyze \
    -v "${HOME}/.m2:/root/.m2" \
    -v "${PWD}:/var/my-app" \
    --link db:db \
    -e "NAROU_CRAWLER_DB_USER=db_user" \
    -e "NAROU_CRAWLER_DB_PASS=db_pass" \
    -e "NAROU_CRAWLER_DB_HOST=db" \
    -e "NAROU_CRAWLER_DB_NAME=narou_analyze" \
    narou-analyze-dev
```

実行用Dockerイメージをビルドします。

```
docker build -t u6kapps/narou-analyze .
```

### 実行

実行用Dockerイメージを起動します。環境変数を設定する必要があることに注意。

```
$ docker-compose up -d
```

## Links

- [narou-analyze - u6k.Redmine](https://redmine.u6k.me/projects/narou-analyze)
- [u6k/narou-analyze - GitHub](https://github.com/u6k/narou-analyze)
- [u6k.blog](http://blog.u6k.me)

## License

[![license](https://img.shields.io/github/license/u6k/narou-analyze.svg)](https://github.com/u6k/narou-analyze/blob/master/LICENSE)
