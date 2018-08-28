function sendBatches({
  requestBody,
  dataArray,
  sendMessage,
  delayBetweenBatches = 100,
  setupFrames = [],
  metaPostProcessing = null
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

    batches.push({
      data: dataArray.slice(i, i + batchSize),
      meta: {
        percent: 100
      }
    });
  }

  const allFrames = setupFrames.concat(batches);
  const delay = process.env.RESPONSE_DELAY;
  for (let i = 0; i < allFrames.length; i++) {
    setTimeout(function(index) {
      return function() {
        const { data, meta } = allFrames[index];

        let message = {
          data,
          meta: {
            ...meta,
            complete: delay && delay > 1 ? (index + 1) === allFrames.length : true
          }
        };

        if (metaPostProcessing && message) {
          message = metaPostProcessing(message, index);
        }

        sendMessage(message);
      };
    }(i), i * delayBetweenBatches);
  }
}

export {
  sendBatches
};
