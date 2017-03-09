function sendBatches({
  requestBody,
  dataArray,
  sendMessage,
  delayBetweenBatches = 100
}) {
  const stream = requestBody.stream || {};
  const page = requestBody.page || {};
  const dataLength = dataArray.length;

  const batch = stream.batch || 10;
  const pageSize = page.size || stream.limit || 100;
  let pageStart = page.index || 0;
  let pageEnd = pageStart + pageSize;

  // invalid ask, just set to 0
  if (pageStart > dataLength) {
    pageStart = 0;
  }

  // Set pageEnd to dataLength if it is larger than data size
  if (pageEnd > dataLength) {
    pageEnd = dataLength;
  }

  const batches = [];
  for (let i = pageStart; i < pageEnd; i += batch) {
    let batchSize = batch;
    if (i + batch > pageEnd) {
      batchSize = pageEnd - i;
    }
    batches.push(dataArray.slice(i, i + batchSize));
  }

  for (let i = 0; i < batches.length; i++) {
    setTimeout(function(index) {
      return function() {
        const dataToSend = {
          data: batches[index]
        };

        dataToSend.meta = { complete: (index + 1) === batches.length };

        sendMessage(dataToSend);
      };
    }(i), i * delayBetweenBatches);
  }
}

export {
  sendBatches
};