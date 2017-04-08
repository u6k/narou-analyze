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
    -d '{"searchPageUrl":"http://yomou.syosetu.com/search.php?notnizi=1&word=&notword=&genre=&order=new&type=","limit":100000}' \
    https://crawler.narou-analyze.u6k.me/api/indexingNovel
```

- searchPageUrl
    - 「小説家になろう」検索画面のURL
- limit
    - 検索するページ数

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
    --name narou-crawler-db \
    -e POSTGRES_PASSWORD=db_pass \
    -e POSTGRES_USER=db_user \
    -e POSTGRES_DB=narou_crawler \
    postgres
```

ActiveMQコンテナを起動します。

```
docker run \
    -d \
    --name narou-crawler-mq \
    webcenter/activemq
```

開発用コンテナを起動します。

```
docker run \
    --rm \
    -v $HOME/.m2:/root/.m2 \
    -v $(pwd):/var/my-app \
    --link narou-crawler-db:db \
    --link narou-crawler-mq:mq \
    -e NAROU_CRAWLER_DB_USER=db_user \
    -e NAROU_CRAWLER_DB_PASS=db_pass \
    -e NAROU_CRAWLER_DB_NAME=narou_crawler \
    -p 8080:8080 \
    u6kapps/narou-crawler-dev mvn spring-boot:run
```

### 実行用Dockerイメージをビルド

PostgreSQLコンテナ、ActiveMQコンテナを起動します。コマンドは上記と同じ。

jarファイルを作成します。

```
docker run \
    --rm \
    -v $HOME/.m2:/root/.m2 \
    -v $(pwd):/var/my-app \
    --link narou-crawler-db:db \
    --link narou-crawler-mq:mq \
    -e NAROU_CRAWLER_DB_USER=db_user \
    -e NAROU_CRAWLER_DB_PASS=db_pass \
    -e NAROU_CRAWLER_DB_NAME=narou_crawler \
    u6kapps/narou-crawler-dev
```

実行用Dockerイメージをビルドします。

```
docker build -t u6kapps/narou-crawler .
```

### 実行

PostgreSQLコンテナを起動します。本番環境なので、パスワードは変更してください。

```
docker run \
    -d \
    --name narou-crawler-db \
    -e POSTGRES_PASSWORD=db_pass \
    -e POSTGRES_USER=db_user \
    -e POSTGRES_DB=narou_crawler \
    -v $HOME/docker-volumes/narou-crawler/db:/var/lib/postgresql/data \
    postgres
```

ActiveMQコンテナを起動します。

```
docker run \
    -d \
    --name narou-crawler-mq \
    -e ACTIVEMQ_REMOVE_DEFAULT_ACCOUNT=true \
    -e ACTIVEMQ_ADMIN_LOGIN=mq_user \
    -e ACTIVEMQ_ADMIN_PASSWORD=mq_pass \
    -e ACTIVEMQ_ENABLED_SCHEDULER=true \
    -v $HOME/docker-volumes/narou-crawler/mq:/data/activemq \
    webcenter/activemq
```

実行用コンテナを起動します。

```
docker run \
    -d \
    --name narou-crawler \
    --link narou-crawler-db:db \
    --link narou-crawler-mq:mq \
    -e NAROU_CRAWLER_DB_USER=db_user \
    -e NAROU_CRAWLER_DB_PASS=db_pass \
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
