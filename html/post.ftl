<!DOCTYPE html>
<html>
<head>
  <link href="https://fonts.googleapis.com/css?family=Roboto:400,500,700" rel="stylesheet">
  <style>
  * {
    margin: 0;
    padding: 0;
  }

  body {
    background: #e94233;
  }

  div.container {
    background: #fff;
    max-width: 768px;
    margin: 32px auto 0;
    box-shadow: 0 3px 8px 0 rgba(0, 0, 0, 0.5);
    border-radius: 4px;
  }
  p.title{
    text-align: left;
    margin: 0px 16px 0px 16px;
    padding: 28px 0px 0px 0px;
    font-size: 18pt;
    font-weight: 500;
    font-family: 'Roboto', sans-serif;
  }
  p.author{
    text-align:left;
    margin: 8px 16px 20px 16px;
    font-size: 10pt;
    color:gray;
    font-family: 'Roboto', sans-serif;
  }
  p.date{
    text-align:left;
    margin: 28px 16px 16px 16px;
    padding: 0px 0px 28px 0px;
    font-size: 10pt;
    color:gray;
    font-family: 'Roboto', sans-serif;
  }
  p.text {
    margin: 28px 16px 28px 16px;
    overflow: hidden;
    font-family: 'Roboto', sans-serif;
  }
  </style>
</head>

<body>
  <div class="container">
    <p class="title">${title}</p>
    <p class="author">${author}</p>
    <p class="text">${text}</p>
    <p class="date">${date}</p>
  </div>
</body>
</style>
</html>
