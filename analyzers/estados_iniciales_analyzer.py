from pathlib import Path

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

#Header csv: algoritmo,seed,iteration,valid,hfAvanzadaValue,hfBasicaValue,peticionesAsignadas,peticionesTotales,
# camionesUsados,camionesTotales,totalKm,kmMedioCamion,beneficioDelDia,beneficio,coste,calidad,errorMessage


# === 1. Cargar el CSV ===
df = pd.read_csv("../data/estados_iniciales.csv")

# --- Configuración ---
outdir = Path("plots/estados_iniciales")
outdir.mkdir(parents=True, exist_ok=True)

# Asegúrate de que las columnas numéricas están en el tipo correcto
cols_numericas = ["hfAvanzadaValue","hfBasicaValue", "beneficioDelDia","beneficio", "coste", "calidad", "totalKm",
                  "kmMedioCamion", "peticionesAsignadas", "peticionesTotales", "camionesUsados", "camionesTotales"]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

# === 2. Agrupar por algoritmo ===
resumen = df.groupby("algoritmo").agg({
    "hfAvanzadaValue": "mean",
    "hfBasicaValue": "mean",
    "beneficioDelDia": "mean",
    "beneficio": "mean",
    "coste": "mean",
    "calidad": "mean",
    "totalKm": "mean",
    "camionesUsados": "mean",
    "peticionesAsignadas": "mean",
}).round(2)

#Mostrar resumen en una tabla
tabla = resumen.copy()
fig, ax = plt.subplots(figsize=(max(8, 1.2*len(tabla.columns)), 0.6*len(tabla)+2))
ax.axis("off")
tbl = ax.table(
    cellText=tabla.values,
    colLabels=tabla.columns,
    rowLabels=tabla.index,
    loc="center"
)
tbl.auto_set_font_size(False)
tbl.set_fontsize(10)
tbl.scale(1, 1.2)  # ancho, alto de celdas
plt.title("Resumen por algoritmo", pad=12)
plt.tight_layout()
plt.savefig(outdir / "tabla_resumen.png", dpi=300)

# === 3. Visualizaciones ===
sns.set_theme(style="whitegrid")

# Asegura el mismo orden de categorías en todos los gráficos
orden_alg = list(df["algoritmo"].unique())

# Helper para nombres de archivo
def slug(s: str) -> str:
    return "".join(c.lower() if c.isalnum() else "-" for c in s).strip("-")

# Especificación de los gráficos que quieres
plots = [
    dict(y="beneficioDelDia",
         title="Beneficio Diario medio (No cuenta peticiones sin asignar)",
         ylabel="Beneficio Diario medio",
         palette="viridis"),
    dict(y="beneficio",
         title="Beneficio medio por algoritmo",
         ylabel="Beneficio medio",
         palette="viridis"),
    dict(y="coste",
         title="Coste medio por algoritmo",
         ylabel="Coste medio",
         palette="magma"),
    dict(y="calidad",
         title="Calidad media por algoritmo",
         ylabel="Calidad media",
         palette="magma"),
]

# --- 1) Guardar cada gráfico como imagen individual ---
for spec in plots:
    fig, ax = plt.subplots(figsize=(8, 5))
    sns.barplot(
        data=df, x="algoritmo", y=spec["y"],
        estimator="mean", ci="sd",
        palette=spec["palette"], order=orden_alg, ax=ax
    )
    ax.set_title(spec["title"])
    ax.set_xlabel("Algoritmo de inicialización")
    ax.set_ylabel(spec["ylabel"])
    plt.tight_layout()

    fname = outdir / f"{slug(spec['title'])}.png"
    fig.savefig(fname, dpi=200, bbox_inches="tight")
    plt.close(fig)  # liberar memoria
    print(f"Guardado: {fname}")

# --- 2) Un solo PNG con todos como subplots ---
fig, axes = plt.subplots(2, 2, figsize=(14, 10), constrained_layout=True)
axes = axes.ravel()

for ax, spec in zip(axes, plots):
    sns.barplot(
        data=df, x="algoritmo", y=spec["y"],
        estimator="mean", ci="sd",
        palette=spec["palette"], order=orden_alg, ax=ax
    )
    ax.set_title(spec["title"])
    ax.set_xlabel("Algoritmo de inicialización")
    ax.set_ylabel(spec["ylabel"])
    # Girar etiquetas si hay muchas categorías
    ax.tick_params(axis="x", labelrotation=20)

# Si tienes menos/más de 4 gráficos, puedes ocultar ejes sobrantes:
for ax in axes[len(plots):]:
    ax.set_visible(False)

combo_path = outdir / "resumen_algoritmos_subplots.png"
fig.savefig(combo_path, dpi=200, bbox_inches="tight")
plt.close(fig)
print(f"Guardado combinado: {combo_path}")

#
# # === 4. Alguna estadística adicional ===
# Peticiones assignadas
plt.figure(figsize=(9,5))
sns.barplot(data=df, x="algoritmo", y="peticionesAsignadas", estimator="mean", ci="sd", palette="Blues")
plt.title("Peticiones assignadas medias por algoritmo")
plt.xlabel("Algoritmo de inicialización")
plt.ylabel("Peticiones assignadas medias")
plt.savefig(outdir / "peticiones_assignadas_medias.png", dpi=300)

# # --- Boxplots para ver la variabilidad ---
plt.figure(figsize=(9,5))
sns.boxplot(data=df, x="algoritmo", y="calidad", palette="Spectral")
plt.title("Distribución de la calidad (beneficio - coste)")
plt.tight_layout()
plt.show()
plt.savefig(outdir / "boxplot_calidad.png", dpi=300)

