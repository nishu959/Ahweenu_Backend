# Render requires a Dockerfile, but we use render.yaml instead
FROM alpine
CMD ["echo", "Using render.yaml – not Docker"]
