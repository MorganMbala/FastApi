using System.Net.Http.Headers;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllersWithViews();

// HttpClient typé pour l'API FastAPI
builder.Services.AddHttpClient("FastApi", client =>
{
    var baseUrl = builder.Configuration["FastApi:BaseUrl"] ?? "http://localhost:8000/";
    client.BaseAddress = new Uri(baseUrl);
    client.DefaultRequestHeaders.Accept.Add(
        new MediaTypeWithQualityHeaderValue("application/json"));
});

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseRouting();
app.UseAuthorization();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Acteurs}/{action=Index}/{id?}");

app.Run();
