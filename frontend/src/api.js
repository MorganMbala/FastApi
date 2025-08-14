const API = import.meta.env.VITE_API_URL || "http://127.0.0.1:8000";

export async function getActeurs() {
  const res = await fetch(`${API}/acteurs`);
  if (!res.ok) throw new Error("Failed to fetch acteurs");
  return res.json();
}

export async function createActeur(acteur) {
  const res = await fetch(`${API}/acteur/`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(acteur),
  });
  if (!res.ok) throw new Error("Failed to create acteur");
  return res.json();
}

export async function updateActeur(id, data) {
  const res = await fetch(`${API}/acteur/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Failed to update acteur");
  return res.json();
}

export async function deleteActeur(id) {
  const res = await fetch(`${API}/acteur/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Failed to delete acteur");
  return res.json();
}

export async function getTotalActeurs() {
  const res = await fetch(`${API}/total_acteurs`)
  if (!res.ok) throw new Error("Failed to fetch total acteurs")
  const data = await res.json()
  return data.total
}
