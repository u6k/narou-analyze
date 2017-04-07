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

- 検索ページから全小説のURLを取得
- 小説のメタ・データ、内容を取得

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

MySQLコンテナを起動します。

```
docker run \
    -d \
    --name narou-crawler-db \
    -e MYSQL_ROOT_PASSWORD=root_pass \
    -e MYSQL_DATABASE=narou_crawler \
    -e MYSQL_USER=narou_crawler_user \
    -e MYSQL_PASSWORD=narou_crawler_pass \
    mysql
```

開発用コンテナを起動します。

```
docker run \
    --rm \
    -v $HOME/.m2:/root/.m2 \
    -v $(pwd):/var/my-app \
    --link narou-crawler-db:db \
    -p 8080:8080 \
    u6kapps/narou-crawler-dev mvn spring-boot:run
```

### 実行用Dockerイメージをビルド

MySQLコンテナを起動します。コマンドは上記と同じ。

jarファイルを作成します。

```
docker run \
    --rm \
    -v $HOME/.m2:/root/.m2 \
    -v $(pwd):/var/my-app \
    --link narou-crawler-db:db \
    u6kapps/narou-crawler-dev
```

実行用Dockerイメージをビルドします。

```
docker build -t u6kapps/narou-crawler .
```

### 実行

MySQLコンテナを起動します。本番環境なので、パスワードは変更してください。

```
docker run \
    -d \
    --name narou-crawler-db \
    -e MYSQL_ROOT_PASSWORD=root_pass \
    -e MYSQL_DATABASE=narou_crawler \
    -e MYSQL_USER=narou_crawler_user \
    -e MYSQL_PASSWORD=narou_crawler_pass \
    -v $HOME/docker-volumes/narou-crawler/db:/var/lib/mysql \
    mysql
```

実行用コンテナを起動します。

```
docker run \
    -d \
    --name narou-crawler \
    --link narou-crawler-db:db \
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
