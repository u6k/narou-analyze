# narou-crawler

[![CircleCI](https://circleci.com/gh/u6k/narou-crawler.svg?style=svg)](https://circleci.com/gh/u6k/narou-crawler)

「小説家になろう」をクローリングして、データを収集します。

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

- searchPageUrl
    - 「小説家になろう」検索画面のURL
    - 検索画面の仕様で100ページ上限があるので、100ページ未満になるような検索画面URLを指定すること

### 小説のメタ・データ、内容を取得

TODO

## Installation

### 開発環境を構築

開発用Dockerイメージをビルドします。

```
docker build -t u6kapps/narou-crawler-dev -f Dockerfile-dev .
```

Eclipseプロジェクトを作成します。

```
docker run \
    --rm \
    -v $HOME/.m2:/root/.m2 \
    -v $(pwd):/var/my-app \
    u6kapps/narou-crawler-dev mvn eclipse:eclipse
```

### 開発環境でアプリケーションを実行

PostgreSQLコンテナを起動します。

```
docker run \
    -d \
    --name db \
    -e POSTGRES_PASSWORD=db_pass \
    -e POSTGRES_USER=db_user \
    -e POSTGRES_DB=narou_crawler \
    postgres
```

開発用コンテナを起動します。

```
docker run \
    --rm \
    --name narou_crawler \
    -v $HOME/.m2:/root/.m2 \
    -v $(pwd):/var/my-app \
    --link db:db \
    -e NAROU_CRAWLER_DB_USER=db_user \
    -e NAROU_CRAWLER_DB_PASS=db_pass \
    -e NAROU_CRAWLER_DB_HOST=db \
    -e NAROU_CRAWLER_DB_NAME=narou_crawler \
    -p 8080:8080 \
    u6kapps/narou-crawler-dev mvn spring-boot:run
```

### 実行用Dockerイメージをビルド

PostgreSQLコンテナを起動します。コマンドは上記と同じ。

jarファイルを作成します。

```
docker run \
    --rm \
    --name narou_crawler \
    -v $HOME/.m2:/root/.m2 \
    -v $(pwd):/var/my-app \
    --link db:db \
    -e NAROU_CRAWLER_DB_USER=db_user \
    -e NAROU_CRAWLER_DB_PASS=db_pass \
    -e NAROU_CRAWLER_DB_HOST=db \
    -e NAROU_CRAWLER_DB_NAME=narou_crawler \
    u6kapps/narou-crawler-dev
```

実行用Dockerイメージをビルドします。

```
docker build -t u6kapps/narou-crawler .
```

### 実行

PostgreSQLコンテナを起動します。本番環境なので、パスワードは変更してください。

実行用コンテナを起動します。

```
docker run \
    -d \
    --name narou-crawler \
    --link db:db \
    -e NAROU_CRAWLER_DB_USER=db_user \
    -e NAROU_CRAWLER_DB_PASS=db_pass \
    -e NAROU_CRAWLER_DB_HOST=db \
    -e NAROU_CRAWLER_DB_NAME=narou_crawler \
    -p 8080:8080 \
    u6kapps/narou-crawler
```

## Links

- GitHub
    - [u6k/narou-crawler](https://github.com/u6k/narou-crawler)
- Author
    - [u6k.blog()](http://blog.u6k.me)

## License

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)
