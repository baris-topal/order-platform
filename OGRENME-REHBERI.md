# Öğrenme Rehberi — 4 Hafta

Her hafta aynı düzende:
1. **Örneği oku** (product-service'te hazır, çalışıyor). Satır satır neden öyle yazıldığını anla.
2. **Alıştırmayı kendin yaz** (order-service'te `TODO` olan yerler). Yapay zeka aracını kullanabilirsin ama
   ürettiği her satırı açıklayabilecek durumda olmalısın.
3. **Mülakat sorularını sesli cevapla.** Takıldığın soru = tekrar etmen gereken konu.
4. **Kontrol listesi** tamamsa CV satırını ekle.

> Altın kural: Bir soruyu "ezberden" değil "bu projede şunu yaptım, çünkü…" diye cevaplayabiliyorsan hazırsın.
> Mülakatta en güçlü cevap kendi kodunu örnek vermektir.

---

## Hafta 1 — Birim test (JUnit 5 + Mockito)

**Oku:** `product-service/.../ProductServiceTest.java`, `ProductControllerTest.java`

**Yap:**
- [ ] `ProductServiceTest` içindeki 2 küçük TODO
- [ ] `ProductControllerTest` içindeki başarılı POST testi
- [ ] `order-service/.../OrderServiceTest.java` — 4 test (dosyadaki açıklamaya göre)
- [ ] Bonus: `OrderControllerTest` (@WebMvcTest) sıfırdan

**Mülakat soruları**
1. Birim test ile entegrasyon testinin farkı ne? Hangisinden neden daha çok yazılır? (test piramidi)
2. `@Mock`, `@InjectMocks`, `@Spy`, `@MockBean` arasındaki fark ne?
3. `@WebMvcTest` neyi yükler, neyi yüklemez? Neden servis katmanını mock'lamak zorundayız?
4. `when(...).thenReturn(...)` ile `doReturn(...).when(...)` farkı? `void` metodu nasıl stub'larsın?
5. `verify(mock, never())` ne zaman işe yarar? Örnek ver. (Cevap: OrderServiceTest #2)
6. `ArgumentCaptor` ne için kullanılır?
7. Mockito `final` sınıfları/statik metodları mock'layabilir mi? (mockito-inline / `mockStatic`)
8. Private metodu nasıl test edersin? (Cevap: etmezsin, public davranış üzerinden test edersin)
9. Given-When-Then (Arrange-Act-Assert) nedir?
10. "Her şeyi mock'lamak" neden kötü olabilir? (Test implementasyona bağlanır, refactor'da kırılır)
11. BigDecimal'da `isEqualTo` yerine neden `isEqualByComparingTo`? (`2.0` ve `2.00` equals'ta farklı)

**CV:** JUnit 5 ve Mockito ile servis ve controller katmanlarına birim testler yazdım.

---

## Hafta 2 — Entegrasyon testi, JaCoCo, CI pipeline

**Oku:** `ProductIntegrationTest.java`, kök `pom.xml` (JaCoCo), `Jenkinsfile`, `.github/workflows/ci.yml`

**Yap:**
- [ ] `ProductIntegrationTest` içindeki 409 senaryosu
- [ ] order-service için `@SpringBootTest` testi: `ProductClient` ve `OrderEventPublisher`'ı `@MockBean` yap,
      POST `/api/orders` → DB'de sipariş oluştu mu kontrol et
- [ ] `mvn verify` çalıştır, `target/site/jacoco/index.html` raporunu aç. Hangi satırlar kırmızı?
- [ ] Kök pom'daki JaCoCo `check` bloğunu aç, eşiği gör, bir testi silip build'in kırıldığını gör
- [ ] Jenkins'i Docker'da çalıştır (`docker run -p 8080:8080 jenkins/jenkins:lts`), repo'yu bağla, pipeline'ı koştur
- [ ] Repoyu GitHub'a at → Actions sekmesinde yeşil tiki gör

**Mülakat soruları**
1. `@SpringBootTest` ile `@WebMvcTest` / `@DataJpaTest` farkı? Hangisi ne kadar yavaş, neden?
2. Testte H2 kullanmanın riski ne? (Prod PostgreSQL; SQL farklılıkları) → Çözüm: Testcontainers
3. Spring test context caching nedir? `@MockBean` neden context'i yeniden yaratır ve testleri yavaşlatır?
4. Testler arası veri izolasyonunu nasıl sağlarsın? (`@BeforeEach` temizlik, `@Transactional` rollback, `@Sql`)
5. `@Transactional` test metoduna konunca ne olur? Ne zaman yanıltıcı olabilir?
6. Line coverage ile branch coverage farkı? %100 coverage = hatasız kod mu?
7. Kaç % coverage hedeflersin? (Doğru cevap: sayıdan çok kritik iş kurallarının test edilmesi)
8. CI ile CD farkı? Pipeline'ında hangi aşamalar var, test kırılınca ne olur?
9. Maven `test` ve `verify` fazları arasındaki fark? Surefire ve Failsafe?
10. Declarative ve scripted Jenkins pipeline farkı?

**CV:** Spring Boot entegrasyon testleri yazdım, JaCoCo ile kapsam ölçtüm ve testleri Jenkins pipeline'ına entegre ettim.

---

## Hafta 3 — Docker

**Oku:** `product-service/Dockerfile`, `docker-compose.yml`, `docker/postgres/init.sql`

**Yap:**
- [ ] `docker build -f product-service/Dockerfile -t product-service .` → `docker images` ile boyuta bak
- [ ] Tek aşamalı (sadece `maven` image'ı ile) bir Dockerfile yaz, boyutu karşılaştır. Farkı not al (CV/mülakat için somut sayı!)
- [ ] `order-service/Dockerfile` yaz
- [ ] `docker-compose.yml`'daki order-service bloğunu aç, `docker compose up --build`
- [ ] Postman/curl ile ürün ekle, `docker compose logs -f product-service` izle
- [ ] Bonus: `.dockerignore` ekle; Jenkinsfile'a Docker Build stage ekle

**Mülakat soruları**
1. Image ile container farkı? Container ile VM farkı?
2. Multi-stage build ne işe yarar? Senin projende image boyutu ne kadar düştü?
3. Dockerfile'da katman (layer) cache nasıl çalışır? Neden önce `pom.xml`, sonra `src` kopyalıyoruz?
4. `CMD` ile `ENTRYPOINT` farkı? `COPY` ile `ADD` farkı?
5. Container'ı neden root olmayan kullanıcıyla çalıştırırız?
6. Compose'da servisler birbirini nasıl buluyor? Neden `localhost` değil `postgres`?
7. `depends_on` servisin hazır olmasını garanti eder mi? (Hayır → `healthcheck` + `condition: service_healthy`)
8. Volume ne işe yarar? Container silinince veri ne olur?
9. Config/şifreleri image'a gömmek neden kötü? (Ortam değişkenleri, secret yönetimi)
10. `ports` ile `expose` farkı?

**CV:** Spring Boot servislerini multi-stage Dockerfile ile containerize ettim; PostgreSQL ve RabbitMQ ile birlikte docker-compose üzerinde çalıştırdım.

---

## Hafta 4 — Mikroservis iletişimi (REST + RabbitMQ)

**Oku:** `order-service/.../OrderService.java`, `ProductClient.java`, `OrderEventPublisher.java`,
`product-service/.../messaging/*`

**Akışı kendi kelimelerinle çiz:**
```
POST /api/orders
  → order-service  --REST GET /api/products/{id}-->  product-service   (senkron: fiyat + stok)
  → siparişi orders DB'ye kaydet
  → "order.created" olayını RabbitMQ'ya yayınla                         (asenkron)
       → product-service dinler → products DB'de stoğu düşer
```

**Yap:**
- [ ] Sistemi compose ile ayağa kaldır, sipariş ver, RabbitMQ panelinde (localhost:15672) mesajı gör
- [ ] product-service'i durdur (`docker compose stop product-service`), sipariş ver → 503 mü geliyor?
- [ ] `ProductClientTest` (dosyadaki açıklamaya göre)
- [ ] **Asıl alıştırma — sipariş iptali:**
  - `OrderStatus.CANCELLED`, `POST /api/orders/{id}/cancel`
  - `OrderCancelledEvent` yayınla (routing key `order.cancelled`)
  - product-service'te yeni kuyruk + binding + listener → stoğu geri ekle (`Product.increaseStock`)
  - Hepsine test yaz
- [ ] Bonus: Listener hata fırlatınca ne oluyor? Dead Letter Queue (DLQ) ekle
- [ ] Bonus: `RestClient`'a timeout ekle

**Mülakat soruları**
1. Monolit ile mikroservis farkı, artıları/eksileri? Her proje mikroservis olmalı mı?
2. Senkron (REST) ve asenkron (mesajlaşma) iletişimi ne zaman seçersin? Projende neden ikisi de var?
3. RabbitMQ'da exchange, queue, binding, routing key nedir? Direct / topic / fanout farkı?
4. Mesaj iki kez gelirse ne olur? Idempotency nasıl sağlanır?
5. Sipariş DB'ye yazıldı ama mesaj gitmedi — ne olur? (Transactional Outbox pattern)
6. Sipariş iki servisi ilgilendiriyor; dağıtık transaction nasıl yönetilir? (Saga: choreography vs orchestration)
7. product-service çökerse order-service ne yapmalı? (Timeout, retry, circuit breaker — Resilience4j)
8. Database per service neden? Servisler aynı tabloyu okuyabilir mi?
9. Kafka ile RabbitMQ farkı?
10. Servisler birbirini nasıl bulur? (Compose DNS, Kubernetes Service, Eureka) API Gateway ne işe yarar?
11. Bu projede bilinen bir race condition var: iki sipariş aynı anda stok kontrolünden geçerse? Nasıl çözersin?
    (Optimistic locking `@Version`, stok rezervasyonu, product-service'in reddedip "OrderRejected" yayınlaması)

**CV:** REST ve RabbitMQ ile haberleşen sipariş ve ürün mikroservisleri geliştirdim; Docker Compose ile çalıştırdım.

---

## Bittiğinde
- [ ] README'deki "What I'd improve next" bölümünü kendi cümlelerinle güncelle (mülakatta "neyi farklı yapardın?" sorusunun cevabı)
- [ ] `AI-NOTLARI.md`'yi doldur
- [ ] Sonraki adım: Kubernetes (minikube) — bu iki servisi Deployment + Service olarak çalıştırmak
