namespace FrontendAspNetCoreMVC.Models
{
    public class Acteur
    {
        public int Id { get; set; }
        public string Name { get; set; } = "";
        public string Bio { get; set; } = "";
        public string Picture { get; set; } = "";
    }

    // Pour Create/Edit (ton API attend name/bio/picture)
    public class ActeurUpdate
    {
        public string Name { get; set; } = "";
        public string Bio { get; set; } = "";
        public string Picture { get; set; } = "";
    }
}
