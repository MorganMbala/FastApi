using System.Text;
using System.Text.Json;
using FrontendAspNetCoreMVC.Models;
using Microsoft.AspNetCore.Mvc;

namespace FrontendAspNetCoreMVC.Controllers
{
    public class ActeursController : Controller
    {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly JsonSerializerOptions _jsonOptions = new(JsonSerializerDefaults.Web);

        public ActeursController(IHttpClientFactory httpClientFactory)
        {
            _httpClientFactory = httpClientFactory;
        }

        private HttpClient Api() => _httpClientFactory.CreateClient("FastApi");

        // ---------- Helpers ----------
        private async Task<T?> ReadJsonAsync<T>(HttpResponseMessage res)
        {
            var json = await res.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<T>(json, _jsonOptions);
        }

        // ---------- Index (liste + compteur) ----------
        // GET: /Acteurs
        public async Task<IActionResult> Index()
        {
            // Liste
            var listRes = await Api().GetAsync("acteurs");
            if (!listRes.IsSuccessStatusCode)
                return Problem($"API error {listRes.StatusCode}");

            var acteurs = await ReadJsonAsync<List<Acteur>>(listRes) ?? new();

            // Compteur
            var countRes = await Api().GetAsync("total_acteurs");
            if (countRes.IsSuccessStatusCode)
            {
                using var s = await countRes.Content.ReadAsStreamAsync();
                using var doc = await JsonDocument.ParseAsync(s);
                ViewBag.Total = doc.RootElement.GetProperty("total").GetInt32();
            }
            else
            {
                ViewBag.Total = acteurs.Count; // fallback
            }

            return View(acteurs);
        }

        // ---------- Create ----------
        // GET: /Acteurs/Create
        public IActionResult Create() => View(new Acteur());

        // POST: /Acteurs/Create
        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Create(Acteur model)
        {
            if (!ModelState.IsValid) return View(model);

            // L’API attend un objet complet avec Id/Name/Bio/Picture
            var payload = JsonSerializer.Serialize(model, _jsonOptions);
            var res = await Api().PostAsync(
                "acteur/",
                new StringContent(payload, Encoding.UTF8, "application/json")
            );

            if (!res.IsSuccessStatusCode)
                return Problem($"API error {res.StatusCode}");

            return RedirectToAction(nameof(Index));
        }

        // ---------- Edit ----------
        // GET: /Acteurs/Edit/5
        public async Task<IActionResult> Edit(int id)
        {
            var res = await Api().GetAsync($"acteur/{id}");
            if (res.StatusCode == System.Net.HttpStatusCode.NotFound)
                return NotFound();
            if (!res.IsSuccessStatusCode)
                return Problem($"API error {res.StatusCode}");

            var data = await ReadJsonAsync<Acteur>(res);
            return View(data);
        }

        // POST: /Acteurs/Edit/5
        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Edit(int id, ActeurUpdate model)
        {
            if (!ModelState.IsValid) return View(model);

            var payload = JsonSerializer.Serialize(model, _jsonOptions);
            var res = await Api().PutAsync(
                $"acteur/{id}",
                new StringContent(payload, Encoding.UTF8, "application/json")
            );

            if (!res.IsSuccessStatusCode)
                return Problem($"API error {res.StatusCode}");

            return RedirectToAction(nameof(Index));
        }

        // ---------- Delete ----------
        // GET: /Acteurs/Delete/5
        public async Task<IActionResult> Delete(int id)
        {
            var res = await Api().GetAsync($"acteur/{id}");
            if (res.StatusCode == System.Net.HttpStatusCode.NotFound)
                return NotFound();
            if (!res.IsSuccessStatusCode)
                return Problem($"API error {res.StatusCode}");

            var data = await ReadJsonAsync<Acteur>(res);
            return View(data);
        }

        // POST: /Acteurs/Delete/5
        [HttpPost, ActionName("Delete"), ValidateAntiForgeryToken]
        public async Task<IActionResult> DeleteConfirmed(int id)
        {
            var res = await Api().DeleteAsync($"acteur/{id}");
            if (!res.IsSuccessStatusCode &&
                res.StatusCode != System.Net.HttpStatusCode.NotFound)
            {
                return Problem($"API error {res.StatusCode}");
            }
            return RedirectToAction(nameof(Index));
        }
    }
}
