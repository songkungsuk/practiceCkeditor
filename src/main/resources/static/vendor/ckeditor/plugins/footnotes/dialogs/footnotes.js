﻿(function () {
  CKEDITOR.dialog.add("footnotesDialog", function (f) {
    return {
      editor_name: !1,
      footnotes_editor: !1,
      dialog_dom_id: !1,
      title: "각주",
      minWidth: 400,
      minHeight: 200,
      contents: [{
        id: "tab-basic",
        label: "Basic Settings",
        elements: [{
          type: "textarea",
          id: "new_footnote",
          "class": "footnote_text",
          label: "새로운 각주:",
          inputStyle: "height: 100px"
        }, {
          type: "text",
          id: "footnote_id",
          name: "footnote_id",
          label: "각주가 존재하지 않습니다.",
          setup: function (b) {
            var e = this.getDialog();
            b = e.getParentEditor();
            var a = e.getElement().findOne("#" +
                this.domId);
            b = b.editable().findOne(".footnotes ol");
            e.dialog_dom_id = this.domId;
            if (null !== b) {
              null === a.findOne("p") ? a.appendHtml(
                      '\x3cp style\x3d"margin-bottom: 10px;"\x3e\x3cstrong\x3e또는:\x3c/strong\x3e 각주를 선택해주세요\x3c/p\x3e\x3col class\x3d"footnotes_list"\x3e\x3c/ol\x3e')
                  : a.findOne("ol").getChildren().toArray().forEach(
                      function (c) {
                        c.remove()
                      });
              var d = "";
              b.find("li").toArray().forEach(function (c) {
                var a = c.getAttribute("data-footnote-id");
                c = c.findOne("cite").getText();
                d += '\x3cli style\x3d"margin-left: 15px;"\x3e\x3cinput type\x3d"radio" name\x3d"footnote_id" value\x3d"'
                    +
                    a + '" id\x3d"fn_' + a + '" /\x3e \x3clabel for\x3d"fn_' + a
                    + '" style\x3d"white-space: normal; display: inline-block; padding: 0 25px 0 5px; vertical-align: top; margin-bottom: 10px;"\x3e'
                    + c + "\x3c/label\x3e\x3c/li\x3e"
              });
              a.find("label,div").toArray().forEach(function (a) {
                a.setStyle("display", "none")
              });
              a.findOne("ol").appendHtml(d);
              a.find('input[type\x3d"radio"]').toArray().forEach(function (c) {
                c.on("change", function () {
                  a.findOne('input[type\x3d"text"]').setValue(c.getValue());
                  e.footnotes_editor.setData("")
                })
              })
            } else {
              a.find("div").toArray().forEach(function (a) {
                a.setStyle("display",
                    "none")
              })
            }
          }
        }]
      }],
      onShow: function () {
        this.setupContent();
        var b = this;
        CKEDITOR.on("instanceLoaded", function (a) {
          b.editor_name = a.editor.name;
          b.footnotes_editor = a.editor
        });
        var e = b.getParentEditor().id;
        CKEDITOR.replaceAll(function (a, d) {
          if (!a.className.match(/footnote_text/)) {
            return !1;
          }
          for (var c = a; (c = c.parentElement) && !c.classList.contains(e);) {
            ;
          }
          if (!c) {
            return !1;
          }
          d.toolbarGroups = [{
            name: "editing",
            groups: ["undo", "find", "selection", "spellchecker"]
          }, {name: "clipboard", groups: ["clipboard"]}, {
            name: "basicstyles", groups: ["basicstyles",
              "cleanup"]
          }];
          d.allowedContent = "br em strong; a[!href]";
          d.enterMode = CKEDITOR.ENTER_BR;
          d.autoParagraph = !1;
          d.height = 80;
          d.resize_enabled = !1;
          d.autoGrow_minHeight = 80;
          d.removePlugins = "footnotes";
          if (c === f.config.footnotesDialogEditorExtraConfig) {
            for (var b in
                c) {
              d[b] = c[b];
            }
          }
          d.on = {
            focus: function (a) {
              a = a.editor.element.getAscendant("tr").getNext();
              a.find('input[type\x3d"radio"]').toArray().forEach(function (a) {
                a.$.checked = !1
              });
              a.findOne('input[type\x3d"text"]').setValue("")
            }
          };
          return !0
        })
      },
      onOk: function () {
        var b = CKEDITOR.instances[this.editor_name],
            e = this.getValueOf("tab-basic", "footnote_id"), a = b.getData();
        if ("" == e) {
          if ("" == a) {
            return;
          }
          f.plugins.footnotes.build(a, !0, f)
        } else {
          f.plugins.footnotes.build(e, !1, f);
        }
        b.destroy();
        (b = this.getElement().findOne("#" + this.dialog_dom_id).findOne("ol"))
        && b.getChildren().toArray().forEach(function (a) {
          a.remove()
        })
      },
      onCancel: function () {
        CKEDITOR.instances[this.editor_name].destroy()
      }
    }
  })
})();