const inputs = {
    userNameInput: document.getElementById("user-name")
}

const fns = {
    selectColor: async (colorName) => {
        const userName = inputs.userNameInput.value;
        if(userName.length == 0) {
            alert("이름을 입력하세요");
        } else {
            const url = `http://localhost:8081/api/select?color=${colorName}&user=${userName}`;
            await fetch(url);
        }
    }
}
