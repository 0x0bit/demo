package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	resp, err := http.Get(`http://www.baidu.com`)

	if err!= nil {
		fmt.Println("http get error")
		return
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err!= nil {
		fmt.Println("ioutil readall error")
		return
	}
	fmt.Println(string(body))
}
