// SEARCH:
const getSoupList = async () => {
  const response = await fetch('http://localhost:8080/soup', {
    method: 'GET',
        headers: {
          'Accept': 'application/json',
          "Content-Type": "application/json",
        },
  });
  let soupTable = document.getElementById("soupTable");
  const soups = await response.json();
  soups.soups.forEach(soup => {
      let row = soupTable.insertRow();
      let idCell = row.insertCell();
      let nameCell = row.insertCell();
      let hotnessCell = row.insertCell();
      idCell.innerText = soup.id;
      nameCell.innerText = soup.name;
      hotnessCell.innerText = soup.hotness;
  });
}

// POST:
const storeSoup = async () => {
  let soupName = document.getElementById("soupNameInput").value;
  let soupHotness = document.getElementById("soupHotnessInput").value;
  const response = await fetch('http://localhost:8080/soup', {
    method: 'POST',
        headers: {
          'Accept': 'application/json',
          "Content-Type": "application/json",
        },
        body: JSON.stringify({name: soupName, hotness: soupHotness})
  });
  const persisted = await response.json();
  let postResult = document.getElementById("postResult");
  postResult.innerText = persisted.id + ", " + persisted.name + ", " + persisted.hotness;
}

// DELETE:
const deleteSoup = async () => {
  let soupId = document.getElementById("soupIdDeleteInput").value;
  const response = await fetch('http://localhost:8080/soup/' + soupId, {
    method: 'DELETE',
        headers: {
          'Accept': 'application/json',
          "Content-Type": "application/json",
        },
  });
  const deletedId = await response.json();
  console.dir(deletedId);
  let deleteResult = document.getElementById("deleteResult");
  deleteResult.innerText = deletedId;
}

// GET:
const getSoup = async () => {
  let soupId = document.getElementById("soupIdGetInput").value;
  const response = await fetch('http://localhost:8080/soup/' + soupId, {
    method: 'GET',
        headers: {
          'Accept': 'application/json',
          "Content-Type": "application/json",
        },
  });
  const soup = await response.json();
  let getResult = document.getElementById("getResult");
  getResult.innerText = soup.id + ", " + soup.name + ", " + soup.hotness;
}