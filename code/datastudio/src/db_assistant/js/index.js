// var data = require('../js/operationList');
var commands;
var errorcodes;
var selectedCmdAlias = "temp";
var isNotSingle = true;
var oTextSelection = "";
var currentSQL = "";
var backString = "";
var rightMenu = "";
var divDefault = "";
var divGoBack = "";
var formerCmd = "";
var cur_cmd = "";

function getSQLHtml(tempValues) {
    var tempSuggestion = "<pre>";
    $.each(tempValues, function(n, v) {
        if (v.alias.indexOf('|') > -1) {
            tempSuggestion = tempSuggestion + "· <a href='#' onclick='selectedCmdAlias=this.innerText;processInput(this.innerText);goBack(\"block\");' class='linkText'>" + v.id + "</a>\r\n";
        } else {
            tempSuggestion = tempSuggestion + "· <a href='#' onclick='selectedCmdAlias=this.innerText;processInput(this.innerText);goBack(\"block\");' class='linkText'>" + v.alias + "</a>\r\n";
        }
    });
    tempSuggestion = tempSuggestion + "</pre>";
    return tempSuggestion;
}

function hideKeyWord(needHide) {
    if (needHide) {
        $('#keywordInfo').hide();
        $("#listStatus").removeClass("hideList").addClass("showList");
    } else {
        $('#keywordInfo').show();
        $("#listStatus").removeClass("showList").addClass("hideList");
    }
}

function hideOrShowKeyWord() {
    if ($("#keywordInfo").is(':visible')) {
        hideKeyWord(true);
    } else {
        hideKeyWord(false);
    }
}

function hideKeyWordList(str) {
    $('#keyword').val(str);
    hideKeyWord(true);
}

function getSQLHtmlForList(tempValues, recordPath) {
    var tempSuggestion = "";
    $.each(tempValues, function(n, v) {
        if (v.alias.indexOf('|') > -1) {
            tempSuggestion = tempSuggestion + "<tr><td align='left' onclick='selectedCmdAlias=this.innerText;processInput(this.innerText);hideKeyWordList(this.innerText);'>" + v.id + "</td></tr>";
        } else {
            tempSuggestion = tempSuggestion + "<tr><td align='left' onclick='selectedCmdAlias=this.innerText;processInput(this.innerText);hideKeyWordList(this.innerText);'>" + v.alias + "</td></tr>";
        }
    });
    $('#keywordInfo').css("height", "300px");
    $('#keywordInfoBody').html(tempSuggestion);
    setCookie("recordDataPath", recordPath);
}

function checkField(keyword) {
    var tempSuggestion = "";
    var tempValues = commands.cmds;
    var temStr = keyword.toUpperCase();
    var rowCount = 0;
    $.each(tempValues, function(n, v) {
        if (v.alias.indexOf(temStr) == 0) {
            rowCount++;
            if (v.alias.indexOf('|') > -1) {
                tempSuggestion = tempSuggestion + "<tr><td align='left' onclick='selectedCmdAlias=this.innerText;processInput(this.innerText);hideKeyWordList(this.innerText);'>" + v.id + "</td></tr>";
            } else {
                tempSuggestion = tempSuggestion + "<tr><td align='left' onclick='selectedCmdAlias=this.innerText;processInput(this.innerText);hideKeyWordList(this.innerText);'>" + v.alias + "</td></tr>";
            }
        }
    });
    if (rowCount < 16) {
        $('#keywordInfo').css("height", (14 * rowCount) + "px");
    } else {
        $('#keywordInfo').css("height", "300px");
    }
    $('#keywordInfoBody').html(tempSuggestion);
    hideKeyWord(false);
}

function processInput(tempId) {
    if ("hideAssistant" == tempId) {
        myCallDBAssistant('hide');
        return;
    }
    if (document.selection) {
        document.selection.empty();
    } else {
        window.getSelection().removeAllRanges();
    }
    if (divGoBack != "") {
        divGoBack.remove ? divGoBack.remove() : divGoBack.removeNode(true);
    }
    if (rightMenu != "") {
        rightMenu.remove ? rightMenu.remove() : rightMenu.removeNode(true);
    }

    tempId = $.trim(tempId);
    hideKeyWord(true);
    if ("" != tempId) {
        var tempIds = tempId.split(/;/);
        tempId = tempIds[tempIds.length - 1];
        if ("" == tempId || "$$" == tempId) tempId = tempIds[tempIds.length - 2];
        var tokens = StringReader(tempId);
        //var tokens=tempId.split(/[ ]+/);
        //alert(JSON.stringify(tokens));
        var tempCmds = [];
        var cur_pos = 0;
        for (var li = 0; li < tokens.length; li++) {
            if (tempCmds.length == 0) {
                tempCmds = commands.find(tokens[li].toUpperCase(), cur_pos);
                if (tempCmds.length != 0) {
                    cur_pos++;
                }
            } else {
                var formmerA = tempCmds;
                tempCmds = commands.findInArray(tempCmds, tokens[li].toUpperCase(), cur_pos);
                if (tempCmds.length != 0 && formmerA != tempCmds) {
                    cur_pos++;
                }
            }
        }
        if (tempCmds.length > 0) {
            var tempSuggestion = "";
            if (tempCmds.length == 1) {
                cur_cmd = tempCmds[0];
                if (cur_cmd.suggestion != "") {
                    if (isNotSingle) myCallDBAssistant('show');
                    /*if(cur_cmd.alias.indexOf('|')>-1)
                    {
                      tempSuggestion="<h4 class='sectionCmdTitle'>"+cur_cmd.id+"</h4>"+cur_cmd.suggestion+tempSuggestion;
                    }else{
                      tempSuggestion="<h4 class='sectionCmdTitle'>"+cur_cmd.alias+"</h4>"+cur_cmd.suggestion+tempSuggestion;
                    }*/
                    tempSuggestion = "<h4 class='sectionCmdTitle'>" + cur_cmd.alias + "</h4>" + cur_cmd.suggestion + tempSuggestion;
                    $('#keyword').val(cur_cmd.alias);
                    if (cur_cmd.subs.length > 0) {
                        var tempSubSuggestion = "";
                        $(cur_cmd.subs).each(function(i, sub) {
                            {
                                var tempAllKeywords = ".*?" + ((sub.id).replace(/\s/g, ".*?"));
                                var reg = new RegExp(tempAllKeywords, 'gi');
                                if (reg.test(tempId) && "" != sub.suggestion) {
                                    tempSubSuggestion = sub.suggestion + tempSubSuggestion;
                                }
                            }
                        });
                        if (tempSubSuggestion != "")
                            tempSuggestion = "<p class=\"sectionSugTitle\">性能优化建议</p>" + tempSubSuggestion + "<hr class='hrStyle'>" + tempSuggestion;
                    }
                }
            } else {
                cur_cmd = commands.getOneFromArray(tempCmds, cur_pos);
                if (cur_cmd != "") {
                    if (isNotSingle) myCallDBAssistant('show');
                    /*if(cur_cmd.alias.indexOf('|')>-1)
                    {
                      tempSuggestion="<h4 class='sectionCmdTitle'>"+cur_cmd.id+"</h4>"+cur_cmd.suggestion+tempSuggestion;
                    }else{
                      tempSuggestion="<h4 class='sectionCmdTitle'>"+cur_cmd.alias+"</h4>"+cur_cmd.suggestion+tempSuggestion;
                    }*/
                    tempSuggestion = "<h4 class='sectionCmdTitle'>" + cur_cmd.alias + "</h4>" + cur_cmd.suggestion + tempSuggestion;
                    $('#keyword').val(cur_cmd.alias);
                    if (cur_cmd.subs.length > 0) {
                        var tempSubSuggestion = "";
                        $(cur_cmd.subs).each(function(i, sub) {
                            {
                                var tempAllKeywords = ".*?" + ((sub.id).replace(/\s/g, ".*?"));
                                var reg = new RegExp(tempAllKeywords, 'gi');
                                if (reg.test(tempId) && "" != sub.suggestion) {
                                    tempSubSuggestion = sub.suggestion + tempSubSuggestion;
                                }
                            }
                        });
                        if (tempSubSuggestion != "")
                            tempSuggestion = "<p class=\"sectionSugTitle\">性能优化建议</p>" + tempSubSuggestion + "<hr class='hrStyle'>" + tempSuggestion;
                    }
                } else {
                    backString = tempId;
                    if (isNotSingle) myCallDBAssistant('show');
                    tempSuggestion = getSQLHtml(tempCmds);
                    formerCmd = "";
                }
            }
            if (cur_cmd != "" && formerCmd == "") {
                $('#divSuggestion').html(tempSuggestion);
                $(document).scrollTop(0);
                assistantHighlight("assistantExamples");
                formerCmd = cur_cmd;
            } else if (cur_cmd != "" && formerCmd != "" && cur_cmd.alias != formerCmd.alias) {
                $('#divSuggestion').html(tempSuggestion);
                $(document).scrollTop(0);
                assistantHighlight("assistantExamples");
                formerCmd = cur_cmd;
            } else if (formerCmd == "") {
                $('#divSuggestion').html(tempSuggestion);
                $(document).scrollTop(0);
            }
        } else {
            backString = tempId;
            $('#divSuggestion').html(getSQLHtml(commands.cmds));
            $(document).scrollTop(0);
            formerCmd = "";
        }
    } else {
        backString = tempId;
        $('#divSuggestion').html(getSQLHtml(commands.cmds));
        $(document).scrollTop(0);
        formerCmd = "";
        //$('#divSuggestion').html("");
        //if(isNotSingle)myCallDBAssistant('hide');
    }
}

function processErrorCode(errorCode) {
    var err = errorcodes.find($.trim(errorCode));
    if (err != "") {
        var tempHtml = "<p class=\"sectionCmdTitle\">" + errorCode + "</p><p><span class=\"sectionTitle\">Descrition</span></p><p><span class=\"sectionText\">" + err.description + "</span></p><p><span class=\"sectionTitle\">SQLSTATE</span></p><p><span class=\"sectionText\">" + err.sqlstate + "</span></p><p><span class=\"sectionTitle\">Possible Causes</span></p><p><span class=\"sectionText\">" + err.cause + "</span></p><p ><span class=\"sectionTitle\">Solution</span</p><p><span class=\"sectionText\">" + err.solution + "</span></p>";
        $('#divSuggestion').html(tempHtml);
        if (isNotSingle) myCallDBAssistant('show');
    }
}

function processSelection1() {
    if (oTextSelection != "") {
        myCallDBAssistantProcessSelection(oTextSelection);
    }
}

function processSelection2() {
    if (oTextSelection != "") {
        myCallDBAssistantProcessSelection2(oTextSelection);
    }
}

function displayVersionNote(curVersion) {
    var divVersionNote = $('<div style="display:none;line-height:22px;background-color:#fefce8;position:fixed;top:10px;margin:2 0 0 2;padding-left:6px;padding-right:6px;border-radius:4px;border:1px solid;border-color:#aaa9a9;"></div>');
    var p1 = $('<p style="margin-top:5px;">请从华为Support上下载FusionInsight LibrA对应版本的知识库。</p>');
    divVersionNote.append(p1);
    $('body').append(divVersionNote);
}

function disableNote(disable) {
    if (disable) {
        if (divDefault != "") {
            divDefault.remove();
            divDefault = "";
        }
    } else {
        divDefault = $('<div style="line-height:22px;background-color:#fefce8;position:fixed;bottom:0px;margin:10 0 0 2;padding-left:6px;padding-right:6px;border-radius:4px;border:1px solid;border-color:#aaa9a9;">');
        var p1 = $('<p style="margin-bottom:0px;">在中间窗口中，<span style="color:#ed6b44">选中SQL语句或错误码</span>，SQL助手会显示对应的<span style="color:#ed6b44">说明信息</span>。</p>');
        var p2 = $('<p align="right" style="margin-top:-5px;margin-bottom:5px;color:#016bd8;"><span style="cursor:hand;" onclick="disableNote(true);myCallDBAssistantDisableNote(\'\');">我知道了</span></p>');
        divDefault.append(p1);
        divDefault.append(p2);
        $('body').append(divDefault);
    }
}

function setCurrentSQL(sql) {
    currentSQL = sql;
}

function setCookie(name, value) {
    var Days = 30;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
    document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
}

function loadSuggestion(docRealPath) {
    commands = new Commands();
    var xml = new ActiveXObject("Microsoft.XMLDOM");
    xml.async = false;
    xml.load(docRealPath + "/suggestion.xml");
    commands.load(xml);
    var valueStr = JSON.stringify(commands);
    getSQLHtmlForList(commands.cmds, docRealPath);

}

function loadErrorcode(docRealPath) {
    errorcodes = new ErrorCodes();
    //var xml = new ActiveXObject("Microsoft.XMLDOM");
    //xml.async = false;
    //xml.load(docRealPath+"/errorcode.xml");
    //errorcodes.load(xml);
    processInput(currentSQL);
    myCallDBAssistant('show');
}

function isHidden(oDiv) {
    var vDiv = oDiv.nextSibling;
    while (vDiv.nodeType == 3) {
        vDiv = vDiv.nextSibling;
    }
    vDiv.style.display = (vDiv.style.display == 'none') ? 'block' : 'none';
    var pngs = oDiv.getElementsByTagName("img");
    switch (pngs[1].style.display) {
        case 'none':
            pngs[0].style.display = 'none';
            pngs[1].style.display = 'inline';
            break;
        case 'inline':
            pngs[0].style.display = 'inline';
            pngs[1].style.display = 'none';
            break;
    }
}

function goBack(display) {
    if (display == "none") {
        divGoBack.remove ? divGoBack.remove() : divGoBack.removeNode(true);
        divGoBack = "";
        processInput(backString);
    } else {
        divGoBack = $('<div class="grammar-item-goback center"></div>');
        var img1 = $('<img src="img/goback.png" border="0" title="返回" onClick="javascript:goBack(\'none\');">');
        divGoBack.append(img1);
        $(".grammar-item-one").append(divGoBack);
    }
}

function readWorkbookFromRemoteFile(callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('get', url, true);
    xhr.responseType = 'arraybuffer';
    xhr.onload = function(e) {
        if (xhr.status == 200) {
            var data = new Uint8Array(xhr.response)
            var workbook = XLSX.read(data, { type: 'array' });
            if (callback) callback(workbook);
        }
    };
    xhr.send();
}
$(function() {
    $("#keywordInfo").scrollBar({
        width: 252
    });
    setTimeout(function() { //用于在指定的毫秒数后调用函数或计算表达式
        myCallDBAssistantLoad('');
    }, 1000);
    $('body').contextmenu(function(event) {
        //hideKeyWordList();
        if (document.selection) {
            oTextSelection = document.selection.createRange().text;
        } else {
            oTextSelection = window.getSelection().toString();
        }
        //var event = event || window.event;
        var leftX = event.pageX;
        var topY = event.pageY;

        if (oTextSelection != "" && rightMenu == "") {
            var bodyW = $('body').width();
            if (bodyW <= leftX + 130) {
                leftX = bodyW - 130;
            }
            rightMenu = $('<div style="box-shadow:-10px 0 10px rgba(255,255,255,0.01),2px 0 3px gray,0 -10px 10px rgba(255,255,255,0.01),2px 2px 3px gray;width:118px;top:' + topY + 'px;left:' + leftX + 'px;padding:2px 2px 2px 2px;position:absolute;background-image:url(img/assistantCopy.png);border: 1px solid gray;font-size:10px;"></div>');
            var span1 = $('<div style="padding-left:30px;margin:0 0 0 0;border:1px solid #F0F0F0;font-size:12px;line-height:22px;cursor:pointer;" onclick="javascript:processSelection1();" onmouseover="this.style.borderColor=\'#AECFF7\'" onmouseout="this.style.borderColor=\'#F0F0F0\'">拷贝</div>');
            //var br1 =$('<br>');
            var span2 = $('<div style="padding-left:30px;margin:0 0 0 0;border:1px solid #F0F0F0;font-size:12px;line-height:22px;cursor:pointer;" onclick="javascript:processSelection2();" onmouseover="this.style.borderColor=\'#AECFF7\'" onmouseout="this.style.borderColor=\'#F0F0F0\'">拷贝到编辑区</div>');
            rightMenu.append(span1);
            //rightMenu.append(br1);
            rightMenu.append(span2);
            $('body').append(rightMenu);
        } else if (oTextSelection != "" && rightMenu != "") {
            var bodyW = $('body').width();
            if (bodyW <= leftX + 130) {
                leftX = bodyW - 130;
            }
            rightMenu.css('left', leftX);
            rightMenu.css('top', topY);
        } else if (rightMenu != "") {
            rightMenu.remove ? rightMenu.remove() : rightMenu.removeNode(true);
            rightMenu = "";
        }
        return false;
    });
    document.onclick = function() {
        if (rightMenu != "") {
            rightMenu.remove ? rightMenu.remove() : rightMenu.removeNode(true);
            rightMenu = "";
        }
    };
    document.onkeydown = function(e) {
        var e = window.event ? window.event : e;
        if (e.ctrlKey && e.keyCode == 67) {
            if (document.selection) {
                oTextSelection = document.selection.createRange().text;
            } else {
                oTextSelection = window.getSelection().toString();
            }
            if (oTextSelection != "") {
                processSelection1();
                return false;
            }
        }
    };
    $("#grammar").click(function() {
        $(this).addClass("active");
        $("#operationList").removeClass("active");
        $("#beginnerGuide").removeClass("active");
        $("#case").removeClass("active");
        location.href = "index_zh.html";
        // $("#caseContent").addClass("hidden");
        // $("#beginnerContent").addClass("hidden");
        // $("#operationListContent").addClass("hidden");
        // $("#grammarContent").removeClass("hidden");
    });

    $("#operationList").click(function() {
        $(this).addClass("active");
        $("#grammar").removeClass("active");
        $("#beginnerGuide").removeClass("active");
        $("#case").removeClass("active");
        location.href = "operationList.html";
        // $("#caseContent").addClass("hidden");
        // $("#beginnerContent").addClass("hidden");
        // $("#operationListContent").removeClass("hidden");
        // $("#grammarContent").addClass("hidden");
    });

    $("#beginnerGuide").click(function() {
        $(this).addClass("active");
        $("#operationList").removeClass("active");
        $("#grammar").removeClass("active");
        $("#case").removeClass("active");
        location.href = "beginnerGuide.html";
        // $("#caseContent").addClass("hidden");
        // $("#beginnerContent").removeClass("hidden");
        // $("#operationListContent").addClass("hidden");
        // $("#grammarContent").addClass("hidden");
    });

    $("#case").click(function() {
        $(this).addClass("active");
        $("#operationList").removeClass("active");
        $("#beginnerGuide").removeClass("active");
        $("#grammar").removeClass("active");
        location.href = "case.html";
        // $("#caseContent").removeClass("hidden");
        // $("#beginnerContent").addClass("hidden");
        // $("#operationListContent").addClass("hidden");
        // $("#grammarContent").addClass("hidden");
    });
    // setCookie("recordDataPath", "Gauss200 OLAP V100R007C10/zh");
});