import { useEffect, useState } from 'react'
import './App.css'
import { getActeurs, createActeur, updateActeur, deleteActeur, getTotalActeurs } from './api'

export default function App() {
  const [acteurs, setActeurs] = useState([])
  const [total, setTotal] = useState(0)
  const [form, setForm] = useState({ id: '', name: '', bio: '', picture: '' })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  // modal state for edit
  const [editing, setEditing] = useState(null) // { id, name, bio, picture }

  // Fallback for images that fail to load
  const handleImgError = (e) => {
    const img = e.currentTarget
    if (img && img.src.indexOf('/placeholder.svg') === -1) {
      img.onerror = null
      img.src = '/placeholder.svg'
    }
  }

  const load = async () => {
    try {
      setLoading(true)
      setError('')
      const [items, count] = await Promise.all([
        getActeurs(),
        getTotalActeurs()
      ])
      setActeurs(items)
      setTotal(count)
    } catch (e) {
      setError(String(e))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const onCreate = async (e) => {
    e.preventDefault()
    try {
      await createActeur({ id: Number(form.id), name: form.name, bio: form.bio, picture: form.picture })
      setForm({ id: '', name: '', bio: '', picture: '' })
      load()
    } catch (e) { alert(e) }
  }

  const openEdit = (a) => setEditing({ ...a })
  const closeEdit = () => setEditing(null)

  const onSaveEdit = async () => {
    try {
      const { id, name, bio, picture } = editing
      await updateActeur(id, { name, bio, picture })
      closeEdit()
      load()
    } catch (e) { alert(e) }
  }

  const onDelete = async (id) => {
    if (confirm(`Supprimer acteur ${id} ?`)) {
      try { await deleteActeur(id); load() } catch (e) { alert(e) }
    }
  }

  return (
    <div className="container">
      <div className="topbar">
        <div className="count-badge">{total}</div>
        <header className="header">
          <h1 className="title">ACTEURS</h1>
          <span className="subtle">Black & White</span>
        </header>

        <form onSubmit={onCreate} className="form">
          <input className="input" name="id" placeholder="id" value={form.id} onChange={onChange} required />
          <input className="input" name="name" placeholder="name" value={form.name} onChange={onChange} required />
          <input className="input" name="bio" placeholder="bio" value={form.bio} onChange={onChange} required />
          <input className="input span-2" name="picture" placeholder="picture url" value={form.picture} onChange={onChange} required />
          <button className="btn btn-primary" type="submit">Créer</button>
        </form>
      </div>

      {loading && <p>Chargement...</p>}
      {error && <p style={{ color: 'crimson' }}>{error}</p>}

      <section className="grid">
        {acteurs.map((a) => (
          <article key={a.id} className="card">
            <img className="card-media" src={a.picture || '/placeholder.svg'} alt={a.name} onError={handleImgError} />
            <div className="card-head">
              <div>
                <div className="card-title">{a.name}</div>
                <div className="card-bio">{a.bio}</div>
              </div>
            </div>
            <div className="card-actions">
              <button className="btn btn-outline" onClick={() => openEdit(a)}>Modifier</button>
              <button className="btn btn-primary" onClick={() => onDelete(a.id)}>Supprimer</button>
            </div>
          </article>
        ))}
      </section>

      {editing && (
        <div className="modal-backdrop" onClick={closeEdit}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-title">Modifier l'acteur #{editing.id}</div>
            <div className="modal-form">
              <div>
                <div className="label">Name</div>
                <input className="input" value={editing.name} onChange={(e)=>setEditing({ ...editing, name:e.target.value })} />
              </div>
              <div>
                <div className="label">Bio</div>
                <textarea className="input textarea" value={editing.bio} onChange={(e)=>setEditing({ ...editing, bio:e.target.value })} />
              </div>
              <div>
                <div className="label">Picture URL</div>
                <input className="input" value={editing.picture} onChange={(e)=>setEditing({ ...editing, picture:e.target.value })} />
              </div>
            </div>
            <div className="modal-actions">
              <button className="btn btn-outline" onClick={closeEdit}>Annuler</button>
              <button className="btn btn-primary" onClick={onSaveEdit}>Enregistrer</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
