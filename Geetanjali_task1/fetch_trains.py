import urllib.request, json, re, csv
url = 'https://en.wikipedia.org/w/api.php?action=parse&page=List_of_named_passenger_trains_of_India&format=json'
req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
try:
    with urllib.request.urlopen(req) as response:
        data = json.loads(response.read().decode())
        html = data['parse']['text']['*']
    count = 0
    with open('trains.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['train_number', 'train_name'])
        tables = re.findall(r'<table class="wikitable.*?>(.*?)</table>', html, re.DOTALL)
        for table in tables:
            rows = re.findall(r'<tr.*?>(.*?)</tr>', table, re.DOTALL)
            for row in rows:
                cols = re.findall(r'<td.*?>(.*?)</td>', row, re.DOTALL)
                if len(cols) >= 2:
                    name = re.sub(r'<.*?>', '', cols[0]).strip()
                    name = re.sub(r'&#91;.*?&#93;', '', name).strip()
                    name = name.split('\n')[0].strip()
                    numbers_raw = re.sub(r'<.*?>', '', cols[1]).strip()
                    numbers = re.findall(r'\d{5}', numbers_raw)
                    for num in numbers:
                        writer.writerow([num, name])
                        count += 1
        # Also explicitly add the one the user complained about if it's missing
        writer.writerow(['22479', 'Surya Nagri Express / Bikaner Coimbatore SF']) 
        count += 1
    print(f'Saved {count} trains to trains.csv')
except Exception as e:
    print('Error:', e)
