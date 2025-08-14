# Ajout de la BD dans mon code 

from fastapi import FastAPI, HTTPException, Path
from dataclasses import asdict, dataclass
from pydantic import BaseModel
import json
from fastapi.middleware.cors import CORSMiddleware


with open("acteurs.json", "r") as f: #Ouvrir le jasonFile en mode lecture 
    acteurs_list = json.load(f)  #lecture du fichier f et conversion en objet python 

# Utiliser l'id de l'acteur comme clé (et non un index enumerate)
list_acteurs = {a["id"]: a for a in acteurs_list}

@dataclass
class Acteur():                     #classe modele 
    id:int 
    name: str 
    bio: str 
    picture: str

class ActeurUpdate(BaseModel):                     #classe modele 
    name: str   
    bio: str
    picture: str

app = FastAPI() #Faire appel a fast api

# Autoriser le frontend React (Vite 5173 / CRA 3000)
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:3000",
        "http://127.0.0.1:3000",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Persistance des donnees = Sauvegarde des donnees dans un fichier json
def save_to_file():
    with open("acteurs.json", "w") as f:  #Ouvrir le fichier en mode ecriture 
        json.dump(list(list_acteurs.values()), f, indent=4)  #Ecrire dans le fichier f et convertir l'objet python en json avec indent pour l'indentation


# Donner le nombre de documents 
@app.get("/total_acteurs")
def get_total_acteurs()-> dict:
    return{"total":len(list_acteurs)}

# Endpoint GET 
@app.get("/acteurs")
def get_all_acteurs()->list[Acteur]:
    res = []
    for a in list_acteurs.values():
        res.append(Acteur(**a))
    return res

# permet de recuperer un acteur en fonction de son id 
@app.get("/acteur/{id}")
def get_acteur_by_id(id:int=Path(ge=1))->Acteur: # ge veut dire >=
    if id not in list_acteurs:  #Verifier que la recherche est dans l'intervalle [1;6]
        raise HTTPException(status_code=404,detail="Cet acteur n'existe pas")
    return Acteur(**list_acteurs[id])

# Creation d'un acteur 
@app.post("/acteur/")
def create_acteur(acteur:Acteur)->Acteur:
    if acteur.id in list_acteurs:
        raise HTTPException(status_code=409, detail=f"Cet acteur {acteur.id} existe deja")
    list_acteurs[acteur.id]=asdict(acteur) #asdict transforme un objet en dictionnaire 
    save_to_file()
    return acteur 

@app.put("/acteur/{id}")
def update_acteur(acteur: ActeurUpdate, id:int = Path(ge=1)) -> Acteur: #ge >= 
    if id not in list_acteurs:
        raise HTTPException(status_code=404, detail=f"Cet acteur avec {id} n'existe pas")
    update_data_acteur = Acteur(id=id, name=acteur.name, bio=acteur.bio, picture=acteur.picture)
    list_acteurs[id] = asdict(update_data_acteur)
    save_to_file()
    return update_data_acteur

@app.delete("/acteur/{id}")
def delete_acteur(id:int=Path(ge=1)) -> Acteur:
    if id in list_acteurs:
        acteur = Acteur(**list_acteurs[id])  # l'operateur ** permet de convertit un objet en dictionnaire
        del list_acteurs[id]
        save_to_file()
        return acteur
    # Si non trouvé, renvoyer une 404 explicite
    raise HTTPException(status_code=404, detail=f"Cet acteur avec {id} n'existe pas")
