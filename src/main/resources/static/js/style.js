let ui_common = {
  /**
   * 로딩화면 show
   */
  fn_showLoading: function () {
    const html = document.querySelector('html');
    const loading_page = document.getElementById("load");
    loading_page.style.display = 'block';
    loading_page.style.opacity = '1';
    html.style.overflow = 'hidden';
    $('.modal-dim').removeClass('off');
  },
  /**
   * 로딩화면 hide
   */
  fn_hideLoading: function () {
    const html = document.querySelector('html');
    const loading_page = document.getElementById("load");
    loading_page.style.opacity = '0'; //서서히 사라지는 효과
    html.style.overflow = 'auto'; //스크롤 방지 해제
    loading_page.style.display = 'none';
    $('.modal-dim').addClass('off');
  },
  /**
   * 모달 show
   * @param id
   * @param dontMove
   */
  fn_showModal: function (id, dontMove = false) {
    const $id = '#' + id
    let modalDim = $('.modal-dim')

    $('#' + id).css('display', 'flex');
    modalDim.removeClass('off')

    if (!dontMove) {
      $($id).css({
        'left': ($(window).width() - $($id).outerWidth()) / 2,
      });
      $($id).draggable();
    }
  },
  /**
   * 모달 hide
   * @param id
   */
  fn_hideModal: function (id) {
    let modalDim = $('.modal-dim')
    const $id = '#' + id;
    $($id).hide();
    modalDim.addClass('off');
  },
  /**
   * alert or confirm modal 제목 및 내용 변경
   * @param id
   * @param title
   * @param message
   */
  fn_setModalText: function (id, title, message) {
    if (title) {
      $('#' + id + 'Title').text(title);
    }
    if (message) {
      $('#' + id + 'Message').html(message);
    }
  },
  /**
   * alert modal 제목 및 내용 설정 후 show
   * @param title
   * @param message
   * @param id
   */
  fn_showAlertModal: function (title, message, id = 'alertModal') {
    ui_common.fn_showModal(id);
    ui_common.fn_setModalText(id, title, message);
  },
  /**
   * alert modal hide
   * @param id
   */
  fn_hideAlertModal: function (id = 'alertModal') {
    $(id).hide();
  },
  /**
   * confirm modal 제목 및 내용 설정 후 show
   * @param id
   * @param title
   * @param message
   */
  fn_showConfirmModal: function (title, message, id = 'confirmModal') {
    if (id === 'deleteModal') {
      $('#deleteInput').val('');
      $('#btnDelete').prop('disabled', true);
    }
    ui_common.fn_showModal(id);
    ui_common.fn_setModalText(id, title, message);
  },
  /**
   * confirm modal 확인 button 이벤트 함수 할당
   * @param id
   * @param event
   */
  fn_setNewEvent: function (id, event) {
    $('#' + id).off('click').on('click', event);
  },
  /**
   * 입력값 비교값 일치 여부에 따라 button disabled
   * @param id
   * @param value
   * @param compareValue
   */
  fn_checkText: function (id, value, compareValue) {
    const button = $('#' + id);
    if (value.toUpperCase() === compareValue) {
      button.prop('disabled', false);
    } else {
      button.prop('disabled', true);
    }
  },
  fn_ckeditorOpen: function (textareaId, toolbarId, data) {
    const toolbarContainer = document.querySelector('#' + toolbarId);
    const textarea = document.querySelector('#' + textareaId);
    DecoupledEditor
    .create(textarea, {
      minHeight: '400px',
      extraPlugins: [this.fn_MyCustomUploadAdapterPlugin],
      image: {
        resizeOptions: [
          {
            name: 'resizeImage:original',
            value: null,
            icon: 'original'
          },
          {
            name: 'resizeImage:50',
            value: '50',
            icon: 'medium'
          },
          {
            name: 'resizeImage:75',
            value: '75',
            icon: 'large'
          }
        ],
        toolbar: [
          'resizeImage:50',
          'resizeImage:75',
          'resizeImage:original'
        ]
      }
    })
    .then(editor => {
      toolbarContainer.appendChild(editor.ui.view.toolbar.element);
      window.editor = editor;
      if (data) {
        editor.setData(data);
      }
      return editor;
    })
    .catch(error => {
      console.error(error);
    });
  },
  fn_MyCustomUploadAdapterPlugin: function (editor) {
    editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
      const atchFlGrpPk = $('#atchFlGrpPk').val();
      if (atchFlGrpPk !== null && atchFlGrpPk !== '') {
        return new uploadAdapter(loader, atchFlGrpPk)
      } else {
        return new uploadAdapter(loader);
      }
    }
  }
}