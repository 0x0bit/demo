package main

import (
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
)

func Download(url string)  {
	client := http.Client{}
	req, _ := http.NewRequest("GET", url, nil)
	// 自定义 header
	req.Header.Set(`User-Agent`, `Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)`)
	// 发送请求
	resp, err := client.Do(req)

	if err != nil {
		log.Fatal("链接获取页面错误", err)
	}

//	函数结束后关闭链接
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil{
		log.Fatal("读取失败", err)
	}
	fmt.Println(string(body))
}

func main() {
	url := "http://www.baidu.com/"
	Download(url)
}
