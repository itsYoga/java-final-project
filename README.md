# Weather App
(1)專案安裝及執行之步驟 :
原先使用jar資料夾執行java -jar finalproject.jar，然而我們測試後打包完成的jar無法辨識utf-8資料，甚至無法連接resource與網路上opendata
還是強烈建議使用原始碼檔案，利用intellij編譯執行src/AppLauncher才能使用，經過測試可能resource jar可能無法讀到，有附上備用lib能重新安裝


(2)demo後新增或修改的地方: 
新增了訂閱想留存的地區，可以按按鈕來訂閱或取消訂閱，能使用方向鍵上與下來選擇訂閱項目
並新增了一些天氣描述(ex:陰天、陰天有午後雷陣雨等)、根據描述產生相對應的天氣圖片
還有修改了當亂打搜尋資料而出現的錯誤提示，原本只在cmd現在有視窗提醒

(3)其他想特別說明之事項:
我們的APP特殊之處為大學搜尋，我們建立了資料庫來存放大學地址，只要搜尋大學名稱就能顯示該處的天氣資訊
有一些大學有當地的監視錄影影像，能看到現在的當地畫面，然而監視影像是從縣市政府的opendata拿的，有些縣市沒有開放(新竹市、台中市等)
而且台灣有太多大學、這個資料庫只能一個個key上去，會耗太多時間，所以有監視影像的只有零星幾間大學(台、成、北、海、中正等)

 
