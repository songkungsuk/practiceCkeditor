/*************************************** 콤포넌트 공통 ***************************************/
const GRANT_TYPE = 'Bearer';
const AUTHORIZATION_HEADER = 'Authorization';
const REFRESH_TOKEN_HEADER = 'refreshToken';
let common = {
  /*
   * 문자열이 빈 문자열인지 체크하여 기본 문자열로 리턴한다.
   * @param : 체크할 문자열
   * @param : 문자열이 비어있을경우 리턴할 기본 문자열
   * EX) common.nvl(val)
   */
  fn_nvl: function (str, defaultStr) {
    if (typeof defaultStr == "undefined" || defaultStr == null || defaultStr
        == "") {
      defaultStr = "";
    }
    if (typeof str == "undefined" || str == null || str == "") {
      str = defaultStr;
    }
    return str;
  },
  /*
   *	object 복제
   *	param: target to clone
   */
  fn_clone: function (obj) {
    if (obj === null || typeof obj !== 'object') {
      return obj;
    }

    let result = Array.isArray(obj) ? [] : {};
    for (let key of Object.keys(obj)) {
      result[key] = common.clone(obj[key]);
    }

    return result;
  },
  fn_bindingData: function (functionNm, data) {
    $(document).trigger(functionNm, data);
  },
  fn_toJson: function ($outline) {
    let o = {};
    let a = $("input,select,textarea", $outline).serializeArray();

    $.each(a, function () {
      if (o[this.name] !== undefined) {
        if (!o[this.name].push) {
          o[this.name] = [o[this.name]];
        }
        o[this.name].push(this.value || '');
      } else {
        o[this.name] = this.value || '';
      }
    });
    return o;
  },
  fn_removeEmptyJsonKey: function (jsonObj) {
    let obj = $.extend({}, jsonObj);
    return $.each(obj, function (key, value) {
      if (value === "" || value === null) {
        delete obj[key];
      } else if (Object.prototype.toString.call(value) === '[object Object]') {
        common.fn_removeEmptyJsonKey(value);
      } else if (Array.isArray(value)) {
        value.forEach(function (el) {
          common.fn_removeEmptyJsonKey(el);
        });
      }
    });
  },
  fn_movePage(page) {
    window.location.href = page;
  },
  fn_submit: function ($outline, options) {
    if (arguments.length == 1) {
      options = $outline;
      $outline = $();
    }
    let opt = $.extend({
      url: '',
      method: 'post',
      data: {},
      id: '__myform',
      target: null,
    }, options);

    let data = common.fn_toJson($outline);
    data = $.extend(data, opt.data);
    data = common.fn_removeEmptyJsonKey(data);
    opt.param = data;
    opt.action = opt.url;

    let formId = opt.id;
    let form = $('form[id=' + formId + ']');

    if (form.length == 0) {
      form = $(document.createElement('form'))
      .attr('id', formId)
      .attr('name', formId)
      .attr('action', opt.action)
      .attr('method', opt.method)
      .attr('target', opt.target)
      .hide();
      $('body').append(form);
    } else {
      form.attr('action', opt.action)
      .attr('method', opt.method)
      .attr('target', opt.target);
    }

    form.empty();

    // 파라미터 Object로 Form에 hidden필드를 생성
    let param = opt.param;
    if ($.isArray(param)) {
      $.each(param, function () {
        $('<input>').attr(
            {type: 'hidden', name: this.name, value: this.value}).appendTo(
            form);
      });
    } else {
      for (let name in param) {
        $('<input>').attr(
            {type: 'hidden', name: name, value: param[name]}).appendTo(form);
      }
    }
    console.log(form);
    form.submit();
  },
  /**
   * 휴대폰 정규식 밸리데이션
   *
   * @param value
   * @param $span
   * @returns 하이픈이 들어간 휴대폰 번호
   */
  fn_validateMobile(value, $span) {
    value = value.replace(/[^0-9]/g, "").substring(0, 11);
    const regex = /^(01[0|1|6|7|8|9]([0-9]{4})?([0-9]{4}))?$/;

    if (value.length >= 0) {
      const isValid = (regex.test(value));
      $span.toggleClass('dn', isValid);
    } else {
      $span.addClass('dn');
    }
    return value.replace(/(^01[0|1|6|7|8|9])([0-9]{4})?([0-9]{4})$/,
        "$1-$2-$3");
  },
  /**
   * 정규식 밸리데이션 및 안 맞을 시 span 태그 표시
   *
   * @param value
   * @param $span
   * @param regex
   */
  fn_validateByRegex(value, $span, regex) {
    if (value.length > 0) {
      const isValid = (regex.test(value));
      $span.toggleClass('dn', isValid);
    } else {
      $span.addClass('dn');
    }
  },
  /**
   * 비밀번호 text/password 토글 이벤트
   *
   * @param $input
   * @param $p
   */
  fn_pwdToggleEvnet($input, $p) {
    if ($input.attr('type') === 'password') {
      $input.attr('type', "text");
      $p.removeClass('icon-pw-off').addClass('icon-pw-on');
    } else {
      $input.attr('type', "password");
      $p.removeClass('icon-pw-on').addClass('icon-pw-off');
    }
  },
  /**
   * id가 있는 배열을 넘기면
   * 한 객체로 id: idVal()로 넘겨준다.
   *
   * @param idArr
   * @returns {{}}
   */
  fn_idArrayToObject(idArray) {
    const idValues = {};

    idArray.forEach(field => {
      const id = field.id;
      const value = $(`#${id}`).val();
      if (value) {
        idValues[id] = value;
      }
    });
    return idValues;
  },
  /**
   * 두개의 객체를 비교한다.
   * 같은 키 값에서 바뀐 value가 있으면 담아서 리턴
   *
   * @param obj
   * @param cObj
   * @returns {{}}
   */
  fn_compareObject(obj, cObj) {
    const changedValues = {};

    for (const key in cObj) {
      if (cObj.hasOwnProperty(key) && obj.hasOwnProperty(key)) {
        if (cObj[key] !== obj[key]) {
          changedValues[key] = cObj[key];
        }
      }
    }

    return changedValues;
  },
  saveAuthTokenToLoacalStorage: function (res) {
    let authToken = res.getResponseHeader(AUTHORIZATION_HEADER);
    let refreshToken = res.getResponseHeader(REFRESH_TOKEN_HEADER);

    if (authToken) {
      authToken = authToken.replace(GRANT_TYPE + ' ', '');
      localStorage.setItem(AUTHORIZATION_HEADER, authToken);
    }

    if (refreshToken) {
      refreshToken = refreshToken.replace(GRANT_TYPE + ' ', '');
      localStorage.setItem(REFRESH_TOKEN_HEADER, refreshToken);
    }
  },
  saveNewAuthTokenToLoacalStorage: function (newAuthToken) {
    localStorage.setItem(AUTHORIZATION_HEADER,
        newAuthToken.replaceAll(GRANT_TYPE + ' ', ''));
  },
  getHeaderToken: function (xhr) {
    /* Authorization header */
    let AUTHORIZATION = localStorage.getItem(AUTHORIZATION_HEADER);
    let REFRESH_TOKEN = localStorage.getItem(REFRESH_TOKEN_HEADER);
    if (AUTHORIZATION) {
      xhr.setRequestHeader(AUTHORIZATION_HEADER,
          GRANT_TYPE + ' ' + AUTHORIZATION);
    }
    if (REFRESH_TOKEN) {
      xhr.setRequestHeader(REFRESH_TOKEN_HEADER,
          GRANT_TYPE + ' ' + REFRESH_TOKEN);
    }
  },
  getAuthTokenByRefeshToken: function (e) {
    let newAuthToken = e.getResponseHeader(AUTHORIZATION_HEADER);
    if (newAuthToken) {
      common.saveNewAuthTokenToLoacalStorage(newAuthToken);
    }
  },
}
/*************************************** 커스터마이징 ***************************************/
/*
 * Jquery serialize 커스터마이징
 * @param : 결과타입 (생략가능)
 */
$.fn.serialize = function () {
  let array = {};
  $(this).find("input,textarea,select,ul").each(function (i) {
    let id = $(this).attr("id");
    let name = $(this).attr("name");
    if ($(this).val()) {
      if ($(this).attr("type") == "checkbox") {
        if ($(this).prop("checked") == true) {
          array[id] = $(this).val();
        }
      } else if ($(this).attr("type") == "radio") {
        if ($(this).prop("checked") == true) {
          array[name] = $(this).val();
        }
      } else {
        if (!id && name) {
          array[name] = $(this).val();
        } else {
          array[id] = $(this).val();
        }
      }
    }
    if ($(this).attr("type") == "file") {
      let picRegComp = $("[for=" + id + "]");//이미지 추가 버튼
      let picImg = $(picRegComp).next(".pic-detail").find("img");//이미지 태그
      if ($(picImg).attr("src")) {
        array[id + "Base64"] = $(picImg).attr("src");
      }
    }
  });
  return array;
}

/*
 * 카카오 주소 검색 API
 * @param : iframe을 넣을 element id (예 : ${'#layer'} 일 경우 'layer'로 param 전달)
 * @param : 주소 검색 모달창 넓이
 * @param : 주소 검색 모달창 높이
 * ex) 주소 검색 iframe을 넣을 div id : 'layer' 생성(${'#layer'}) > onclick="openDaumPostcode('layer',500,400)"
 */
function fn_openDaumPostcode(elementId, width, height) {
  let elementLayer = document.getElementById(elementId); // iframe을 넣을 element
  elementLayer.style.display = 'none';
  elementLayer.style.position = 'fixed';
  elementLayer.style.overflow = 'hidden';
  elementLayer.style.zIndex = 1;

  new daum.Postcode({
    oncomplete: function (data) {
      const {
        jibunAddress,
        roadAddress,
        userSelectedType: kkoAddrType,
        zonecode: zipcd
      } = data;
      const address = roadAddress;
      $('#kkoAddrType').val(kkoAddrType);
      $('#addr').val(address).trigger('change');
      $('#addrDetail').val('');
      $('#zipcd').val(zipcd).trigger('change');

      closeDaumPostcode(elementId); // 주소 검색 닫기
    },
    width: '100%',
    height: '100%',
    maxSuggestItems: 5
  }).embed(elementLayer);

  // iframe을 넣은 element를 보이게 한다.
  elementLayer.style.display = 'block';

  // iframe을 넣은 element의 위치를 화면의 가운데로 이동시킨다.
  initLayerPosition(elementId, width, height);

  // 주소 검색 닫는 div click event
  clickBodyElement(elementId);
}

function initLayerPosition(elementId, width, height) {
  let borderWidth = 1;	// 주소 검색 모달창 테두리 두께

  let elementLayer = document.getElementById(elementId); // iframe을 넣을 element

  // 위에서 선언한 값들을 실제 element에 넣는다.
  elementLayer.style.width = width + 'px';
  elementLayer.style.height = height + 'px';
  elementLayer.style.border = borderWidth + 'px solid';
  // 실행되는 순간의 화면 너비와 높이 값을 가져와서 중앙에 뜰 수 있도록 위치를 계산한다.
  elementLayer.style.left = (((window.innerWidth
          || document.documentElement.clientWidth) - width) / 2 - borderWidth)
      + 'px';
  elementLayer.style.top = (((window.innerHeight
          || document.documentElement.clientHeight) - height) / 2 - borderWidth)
      + 'px';
  elementLayer.style.zIndex = "9999";

  // iframe을 넣은 element를 제외한 바깥 div 생성
  let bodyElement = document.createElement('div');
  bodyElement.setAttribute("id", "bodyElement");
  bodyElement.style.backgroundColor = "rgba(128, 128, 128, 0.9)";
  bodyElement.style.width = "100%";
  bodyElement.style.height = "100%";
  bodyElement.style.position = "fixed";
  bodyElement.style.zIndex = "9999";

  // iframe을 넣은 element를 제외한 바깥 div body에 추가
  document.body.insertBefore(bodyElement, document.body.firstChild);
}

function clickBodyElement(elementId) {
  $('#bodyElement').on({
    click() {
      closeDaumPostcode(elementId);
    }
  })
}

function closeDaumPostcode(elementId) {
  let elementLayer = document.getElementById(elementId); // iframe을 넣은 element
  let bodyElement = document.getElementById('bodyElement'); // iframe을 넣은 element를 제외한 바깥 div

  // iframe을 넣은 element를 안보이게 한다.
  elementLayer.style.display = 'none';

  // iframe을 넣은 element를 제외한 바깥 div 제거
  document.body.removeChild(bodyElement);

  $('#bodyElement').off('click');
}

function ajaxError(error) {
  if (error.responseJSON) {
    if (!error.responseJSON.message || error.responseJSON.message
        === 'No message available') {
      ui_common.fn_showAlertModal('에러', error.responseJSON.error);
    } else {
      ui_common.fn_showAlertModal('에러', error.responseJSON.message);
    }
  }
}