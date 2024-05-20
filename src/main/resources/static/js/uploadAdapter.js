class uploadAdapter {
  constructor(loader, atchFlGrpPk = null) {
    this.loader = loader;
    this.atchFlGrpPk = atchFlGrpPk;
  }

  async upload() {
    return this.loader.file.then(file => new Promise(((resolve, reject) => {
      let formData = new FormData();
      formData.append('upload', file);
      if (this.atchFlGrpPk != null) {
        formData.append('atchFlGrpPk', this.atchFlGrpPk);
      }
      $.ajax({
        url: '/api/v1/board/upload-image',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function (xhr) {
          common.getHeaderToken(xhr);
        },
        success: function (data, status, res) {
          common.saveAuthTokenToLoacalStorage(res);
          $('#atchFlGrpPk').val(data.atchFlGrpPk);
          resolve({default: data.url});
        },
        error: function (error) {
          console.log(error);
          reject(error);
        }
      });
    })));
  }
}