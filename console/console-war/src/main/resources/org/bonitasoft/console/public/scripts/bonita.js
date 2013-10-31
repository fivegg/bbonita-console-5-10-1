function getParams(url, keepGWTState) {
	var fromIndex = url.indexOf("?");
	if (fromIndex > -1) {
		var parameters = url.substring(fromIndex, url.length);
		if (parameters.length > 1) {
			parameters = parameters.substring(1, parameters.length);
			var uiModeAdminReg = new RegExp("(&?ui=admin&?)", "g");
			var uiModeUserReg = new RegExp("(&?ui=user&?)", "g");
			// remove un-necessary part of the URL
			parameters = parameters.replace(uiModeAdminReg, "");
			parameters = parameters.replace(uiModeUserReg, "");
			if (!keepGWTState) {
				// remove the GWT history token
				var historyTokenReg = new RegExp("(#.*)", "g");
				// remove un-necessary part of the URL
				parameters = parameters.replace(historyTokenReg, "");
			}
			return parameters;
		}
	} else if (keepGWTState) {
		fromIndex = url.indexOf("#");
		if (fromIndex > -1) {
			var parameters = url.substring(fromIndex, url.length);
			if (parameters.length > 1) {
				return parameters;
			}
		}
	}
	return "";
}

function toggleMenu(menuName) {
	var menu = document.getElementById(menuName);
	/*
	 * var menuContent = document.getElementById(menuName + 'Wrapper');
	 * if(menuContent.style.display == 'none'){ menuContent.style.display =
	 * 'block'; } else if((menuContent.style.display == 'block') ||
	 * (menuContent.style.display == '')) { menuContent.style.display = 'none'; }
	 */
	if (menu.className == 'collapsible_widget collapsible_widget-open') {
		menu.className = 'collapsible_widget collapsible_widget-closed';
	} else if (menu.className == 'collapsible_widget collapsible_widget-closed') {
		menu.className = 'collapsible_widget collapsible_widget-open';
	}
}
