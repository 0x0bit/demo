package main

import (
	"fmt"
	"github.com/jackdanger/collectlinks"
	"net/http"
	"net/url"
	"os"
	"strconv"
	"strings"
	"time"
)

var isVisited = make(map[string]bool)

func downloadUrl(url string, queue chan string)  {
	isVisited[url] = true
	// 设置超时时间
	timeout := time.Duration(5 * time.Second)
	client := &http.Client{
		Timeout:timeout,
	}

	req, _ := http.NewRequest("GET", url, nil)
	// 自定义Header
	req.Header.Set("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)")
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("http get error", err)
		return
	}

	// 请求结束后关闭
	defer resp.Body.Close()

	// 获取网页所以链接
	links := collectlinks.All(resp.Body)


	for i, link := range links {
		// 去重和转为绝对路径
		absolute := UrlJoins(link, url)
		if url !="" {
			if !isVisited[absolute] {
				fmt.Println("parse url--->",strconv.Itoa(i),"--->", absolute)
				traceFile(`input.txt`, absolute, i)
				go func() {
					queue <- absolute
				}()
			}
		}
	}
}

func UrlJoins(href, base string) string {
	uri, err := url.Parse(href)
	if err != nil {
		return ""
	}
	baseUrl, err := url.Parse(base)
	if err != nil {
		return ""
	}
	return baseUrl.ResolveReference(uri).String()
}

/**
	追加写入文件
 */
func traceFile(fileName,strContent string, index int)  {
	fd,_:=os.OpenFile(fileName, os.O_RDWR|os.O_CREATE|os.O_APPEND,0644)
	fdTime :=time.Now().Format(`2006-01-02 15:04:05`)
	fdContent :=strings.Join([]string{fdTime,"===<",strconv.Itoa(index), ">===", strContent,"\n"},"")
	buf:=[]byte(fdContent)
	_, _ = fd.Write(buf)
	_ = fd.Close()
}

func main() {
	hostUrl := `http://www.baidu.com/`
	queue := make(chan string,)
	go func() {
		queue <- hostUrl
	}()
	for uri:= range queue {
		downloadUrl(uri, queue)
	}
}